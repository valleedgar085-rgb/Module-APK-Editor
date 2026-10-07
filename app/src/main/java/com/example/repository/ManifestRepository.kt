package com.example.repository

import android.content.Context
import com.example.model.ManifestActivity
import com.example.model.ManifestData
import com.example.model.ManifestProfile
import com.example.util.ManifestXmlParser
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ManifestRepository(private val context: Context) {

    private val manifestFile: File
        get() = File(context.filesDir, "AndroidManifest.xml")

    private val profilesFile: File
        get() = File(context.filesDir, "manifest_profiles.json")

    private val historyFile: File
        get() = File(context.filesDir, "manifest_history.json")

    init {
        // Initialize with default or loaded manifest if not already present
        if (!manifestFile.exists()) {
            val initial = getSampleDefaultManifest()
            val serialized = ManifestXmlParser.serialize(initial)
            manifestFile.writeText(serialized)
        }
    }

    fun loadCurrentManifest(): ManifestData {
        return try {
            if (manifestFile.exists()) {
                val content = manifestFile.readText()
                ManifestXmlParser.parse(content)
            } else {
                getSampleDefaultManifest()
            }
        } catch (e: Exception) {
            getSampleDefaultManifest()
        }
    }

    fun saveManifest(data: ManifestData): String {
        val serializedXml = ManifestXmlParser.serialize(data)
        manifestFile.writeText(serializedXml)
        // Record into history
        recordHistory(data)
        return serializedXml
    }

    fun resetToDefault(): ManifestData {
        val defaultData = getSampleDefaultManifest()
        saveManifest(defaultData)
        return defaultData
    }

    fun importXml(xmlString: String): ManifestData {
        val parsed = ManifestXmlParser.parse(xmlString)
        saveManifest(parsed)
        return parsed
    }

    fun getExportXml(): String {
        return if (manifestFile.exists()) manifestFile.readText() else ManifestXmlParser.serialize(loadCurrentManifest())
    }

    // Profiles / Presets
    fun getPresets(): List<ManifestProfile> {
        val list = mutableListOf<ManifestProfile>()
        // Built-in presets
        list.add(
            ManifestProfile(
                id = "preset_production",
                name = "Production Release",
                description = "Standard production setup, debuggable false, basic permissions",
                timestamp = System.currentTimeMillis(),
                manifest = ManifestData(
                    packageName = "com.aistudio.apkeasyedit.release",
                    versionCode = 10,
                    versionName = "2.1.0",
                    appLabel = "APK Easy Edit Pro",
                    minSdk = 24,
                    targetSdk = 35,
                    compileSdk = 35,
                    isDebuggable = false,
                    allowBackup = false,
                    supportsRtl = true,
                    usesCleartextTraffic = false,
                    permissions = listOf(
                        "android.permission.INTERNET",
                        "android.permission.ACCESS_NETWORK_STATE"
                    ),
                    activities = listOf(
                        ManifestActivity(
                            name = ".MainActivity",
                            label = "APK Easy Edit Pro",
                            exported = true,
                            isLauncher = true
                        ),
                        ManifestActivity(
                            name = ".SettingsActivity",
                            label = "Settings",
                            exported = false,
                            isLauncher = false
                        )
                    )
                )
            )
        )
        list.add(
            ManifestProfile(
                id = "preset_debug",
                name = "Development / Debug",
                description = "Debuggable enabled, cleartext traffic allowed, storage permissions",
                timestamp = System.currentTimeMillis(),
                manifest = ManifestData(
                    packageName = "com.aistudio.apkeasyedit.debug",
                    versionCode = 1,
                    versionName = "1.0.0-dev",
                    appLabel = "APK Easy Edit (Debug)",
                    minSdk = 21,
                    targetSdk = 34,
                    compileSdk = 34,
                    isDebuggable = true,
                    allowBackup = true,
                    supportsRtl = true,
                    usesCleartextTraffic = true,
                    requestLegacyExternalStorage = true,
                    permissions = listOf(
                        "android.permission.INTERNET",
                        "android.permission.ACCESS_NETWORK_STATE",
                        "android.permission.VIBRATE",
                        "android.permission.POST_NOTIFICATIONS"
                    ),
                    activities = listOf(
                        ManifestActivity(
                            name = ".MainActivity",
                            label = "APK Easy Edit Dev",
                            exported = true,
                            isLauncher = true
                        )
                    )
                )
            )
        )
        list.add(
            ManifestProfile(
                id = "preset_kiosk",
                name = "Game / Immersive Landscape",
                description = "Fixed landscape orientation, sound/vibration permissions, high targetSdk",
                timestamp = System.currentTimeMillis(),
                manifest = ManifestData(
                    packageName = "com.aistudio.game.edition",
                    versionCode = 5,
                    versionName = "1.5.2",
                    appLabel = "Arcade Edition",
                    minSdk = 26,
                    targetSdk = 35,
                    compileSdk = 35,
                    isDebuggable = false,
                    allowBackup = true,
                    supportsRtl = false,
                    usesCleartextTraffic = false,
                    permissions = listOf(
                        "android.permission.INTERNET",
                        "android.permission.VIBRATE",
                        "android.permission.WAKE_LOCK"
                    ),
                    activities = listOf(
                        ManifestActivity(
                            name = ".GameMainActivity",
                            label = "Arcade Edition",
                            exported = true,
                            isLauncher = true,
                            screenOrientation = "landscape"
                        )
                    )
                )
            )
        )

        // Load custom user profiles from disk
        if (profilesFile.exists()) {
            try {
                val jsonArray = JSONArray(profilesFile.readText())
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = obj.getString("id")
                    val name = obj.getString("name")
                    val desc = obj.optString("description", "Custom saved preset")
                    val ts = obj.optLong("timestamp", System.currentTimeMillis())
                    val xml = obj.getString("xml")
                    val manifest = ManifestXmlParser.parse(xml)
                    list.add(ManifestProfile(id, name, desc, ts, manifest))
                }
            } catch (_: Exception) {}
        }

        return list
    }

    fun saveCustomPreset(name: String, description: String, manifest: ManifestData) {
        val existingList = mutableListOf<JSONObject>()
        if (profilesFile.exists()) {
            try {
                val arr = JSONArray(profilesFile.readText())
                for (i in 0 until arr.length()) {
                    existingList.add(arr.getJSONObject(i))
                }
            } catch (_: Exception) {}
        }

        val newObj = JSONObject().apply {
            put("id", "custom_${System.currentTimeMillis()}")
            put("name", name)
            put("description", description)
            put("timestamp", System.currentTimeMillis())
            put("xml", ManifestXmlParser.serialize(manifest))
        }
        existingList.add(newObj)
        profilesFile.writeText(JSONArray(existingList).toString())
    }

    fun getHistory(): List<ManifestProfile> {
        val list = mutableListOf<ManifestProfile>()
        if (historyFile.exists()) {
            try {
                val arr = JSONArray(historyFile.readText())
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val id = obj.getString("id")
                    val name = obj.getString("name")
                    val desc = obj.getString("description")
                    val ts = obj.getLong("timestamp")
                    val xml = obj.getString("xml")
                    list.add(ManifestProfile(id, name, desc, ts, ManifestXmlParser.parse(xml)))
                }
            } catch (_: Exception) {}
        }
        return list.reversed()
    }

    private fun recordHistory(data: ManifestData) {
        try {
            val list = mutableListOf<JSONObject>()
            if (historyFile.exists()) {
                val arr = JSONArray(historyFile.readText())
                for (i in 0 until arr.length()) {
                    list.add(arr.getJSONObject(i))
                }
            }
            val record = JSONObject().apply {
                put("id", "hist_${System.currentTimeMillis()}")
                put("name", data.appLabel.ifBlank { "Untitled App" })
                put("description", "Version ${data.versionName} (${data.versionCode})")
                put("timestamp", System.currentTimeMillis())
                put("xml", ManifestXmlParser.serialize(data))
            }
            list.add(record)
            // keep up to 20 recent saves
            val trimmed = if (list.size > 20) list.takeLast(20) else list
            historyFile.writeText(JSONArray(trimmed).toString())
        } catch (_: Exception) {}
    }

    private fun getSampleDefaultManifest(): ManifestData {
        return ManifestData(
            packageName = "com.aistudio.apkeasyedit.fndmsz",
            versionCode = 1,
            versionName = "1.0.0",
            appLabel = "APK Easy Edit",
            minSdk = 24,
            targetSdk = 36,
            compileSdk = 36,
            isDebuggable = false,
            allowBackup = true,
            supportsRtl = true,
            usesCleartextTraffic = false,
            requestLegacyExternalStorage = false,
            iconDrawable = "@mipmap/ic_launcher",
            roundIconDrawable = "@mipmap/ic_launcher_round",
            theme = "@style/Theme.MyApplication",
            permissions = listOf(
                "android.permission.INTERNET",
                "android.permission.ACCESS_NETWORK_STATE"
            ),
            activities = listOf(
                ManifestActivity(
                    name = ".MainActivity",
                    label = "@string/app_name",
                    exported = true,
                    isLauncher = true,
                    screenOrientation = "unspecified"
                )
            )
        )
    }
}
