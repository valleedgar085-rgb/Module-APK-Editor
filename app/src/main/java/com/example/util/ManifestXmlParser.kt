package com.example.util

import android.util.Xml
import com.example.model.ManifestActivity
import com.example.model.ManifestData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer
import java.io.StringReader
import java.io.StringWriter

object ManifestXmlParser {

    private const val ANDROID_NS = "http://schemas.android.com/apk/res/android"
    private const val TOOLS_NS = "http://schemas.android.com/tools"

    /**
     * Parses an AndroidManifest.xml string into ManifestData.
     */
    fun parse(xmlString: String): ManifestData {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
        parser.setInput(StringReader(xmlString))

        var packageName = "com.example.app"
        var versionCode = 1
        var versionName = "1.0.0"
        var compileSdk = 34
        var appLabel = "My Application"
        var minSdk = 24
        var targetSdk = 34
        var isDebuggable = false
        var allowBackup = true
        var supportsRtl = true
        var usesCleartextTraffic = false
        var requestLegacyExternalStorage = false
        var iconDrawable = "@mipmap/ic_launcher"
        var roundIconDrawable = "@mipmap/ic_launcher_round"
        var theme = "@style/Theme.MyApplication"
        val permissions = mutableListOf<String>()
        val activities = mutableListOf<ManifestActivity>()

        var eventType = parser.eventType
        var currentActivity: ManifestActivityBuilder? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "manifest" -> {
                            parser.getAttributeValue(null, "package")?.let { packageName = it }
                            parser.getAttributeValue(ANDROID_NS, "versionCode")?.toIntOrNull()?.let {
                                versionCode = it
                            }
                            parser.getAttributeValue(ANDROID_NS, "versionName")?.let {
                                versionName = it
                            }
                            parser.getAttributeValue(ANDROID_NS, "compileSdkVersion")?.toIntOrNull()?.let {
                                compileSdk = it
                            }
                        }
                        "uses-sdk" -> {
                            parser.getAttributeValue(ANDROID_NS, "minSdkVersion")?.toIntOrNull()?.let {
                                minSdk = it
                            }
                            parser.getAttributeValue(ANDROID_NS, "targetSdkVersion")?.toIntOrNull()?.let {
                                targetSdk = it
                            }
                        }
                        "uses-permission" -> {
                            val perm = parser.getAttributeValue(ANDROID_NS, "name")
                            if (!perm.isNullOrBlank() && !permissions.contains(perm)) {
                                permissions.add(perm)
                            }
                        }
                        "application" -> {
                            parser.getAttributeValue(ANDROID_NS, "label")?.let { appLabel = it }
                            parser.getAttributeValue(ANDROID_NS, "icon")?.let { iconDrawable = it }
                            parser.getAttributeValue(ANDROID_NS, "roundIcon")?.let { roundIconDrawable = it }
                            parser.getAttributeValue(ANDROID_NS, "theme")?.let { theme = it }
                            parser.getAttributeValue(ANDROID_NS, "debuggable")?.let {
                                isDebuggable = it.equals("true", ignoreCase = true)
                            }
                            parser.getAttributeValue(ANDROID_NS, "allowBackup")?.let {
                                allowBackup = it.equals("true", ignoreCase = true)
                            }
                            parser.getAttributeValue(ANDROID_NS, "supportsRtl")?.let {
                                supportsRtl = it.equals("true", ignoreCase = true)
                            }
                            parser.getAttributeValue(ANDROID_NS, "usesCleartextTraffic")?.let {
                                usesCleartextTraffic = it.equals("true", ignoreCase = true)
                            }
                            parser.getAttributeValue(ANDROID_NS, "requestLegacyExternalStorage")?.let {
                                requestLegacyExternalStorage = it.equals("true", ignoreCase = true)
                            }
                        }
                        "activity" -> {
                            val actName = parser.getAttributeValue(ANDROID_NS, "name") ?: ""
                            val actLabel = parser.getAttributeValue(ANDROID_NS, "label") ?: ""
                            val actExported = parser.getAttributeValue(ANDROID_NS, "exported")?.equals("true", ignoreCase = true) ?: false
                            val actOrientation = parser.getAttributeValue(ANDROID_NS, "screenOrientation") ?: "unspecified"
                            currentActivity = ManifestActivityBuilder(
                                name = actName,
                                label = actLabel,
                                exported = actExported,
                                screenOrientation = actOrientation
                            )
                        }
                        "action" -> {
                            val actionName = parser.getAttributeValue(ANDROID_NS, "name")
                            if (actionName == "android.intent.action.MAIN") {
                                currentActivity?.hasMainAction = true
                            }
                        }
                        "category" -> {
                            val catName = parser.getAttributeValue(ANDROID_NS, "name")
                            if (catName == "android.intent.category.LAUNCHER") {
                                currentActivity?.hasLauncherCategory = true
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (tagName == "activity" && currentActivity != null) {
                        activities.add(
                            ManifestActivity(
                                name = currentActivity.name,
                                label = currentActivity.label,
                                exported = currentActivity.exported,
                                isLauncher = currentActivity.hasMainAction && currentActivity.hasLauncherCategory,
                                screenOrientation = currentActivity.screenOrientation
                            )
                        )
                        currentActivity = null
                    }
                }
            }
            eventType = parser.next()
        }

        return ManifestData(
            packageName = packageName,
            versionCode = versionCode,
            versionName = versionName,
            appLabel = appLabel,
            minSdk = minSdk,
            targetSdk = targetSdk,
            compileSdk = compileSdk,
            isDebuggable = isDebuggable,
            allowBackup = allowBackup,
            supportsRtl = supportsRtl,
            usesCleartextTraffic = usesCleartextTraffic,
            requestLegacyExternalStorage = requestLegacyExternalStorage,
            iconDrawable = iconDrawable,
            roundIconDrawable = roundIconDrawable,
            theme = theme,
            permissions = permissions,
            activities = activities,
            rawXml = xmlString
        )
    }

    /**
     * Serializes ManifestData into standard pretty-printed AndroidManifest.xml string.
     */
    fun serialize(data: ManifestData): String {
        val writer = StringWriter()
        val serializer: XmlSerializer = Xml.newSerializer()
        serializer.setOutput(writer)
        serializer.startDocument("utf-8", null)
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)
        serializer.setPrefix("android", ANDROID_NS)
        serializer.setPrefix("tools", TOOLS_NS)

        serializer.startTag("", "manifest")
        serializer.attribute("", "package", data.packageName)
        serializer.attribute(ANDROID_NS, "versionCode", data.versionCode.toString())
        serializer.attribute(ANDROID_NS, "versionName", data.versionName)
        if (data.compileSdk > 0) {
            serializer.attribute(ANDROID_NS, "compileSdkVersion", data.compileSdk.toString())
        }

        // uses-sdk
        serializer.startTag("", "uses-sdk")
        serializer.attribute(ANDROID_NS, "minSdkVersion", data.minSdk.toString())
        serializer.attribute(ANDROID_NS, "targetSdkVersion", data.targetSdk.toString())
        serializer.endTag("", "uses-sdk")

        // permissions
        for (perm in data.permissions) {
            serializer.startTag("", "uses-permission")
            serializer.attribute(ANDROID_NS, "name", perm)
            serializer.endTag("", "uses-permission")
        }

        // application
        serializer.startTag("", "application")
        serializer.attribute(ANDROID_NS, "allowBackup", data.allowBackup.toString())
        serializer.attribute(ANDROID_NS, "icon", data.iconDrawable)
        serializer.attribute(ANDROID_NS, "label", data.appLabel)
        serializer.attribute(ANDROID_NS, "roundIcon", data.roundIconDrawable)
        serializer.attribute(ANDROID_NS, "supportsRtl", data.supportsRtl.toString())
        serializer.attribute(ANDROID_NS, "theme", data.theme)
        if (data.isDebuggable) {
            serializer.attribute(ANDROID_NS, "debuggable", "true")
        }
        if (data.usesCleartextTraffic) {
            serializer.attribute(ANDROID_NS, "usesCleartextTraffic", "true")
        }
        if (data.requestLegacyExternalStorage) {
            serializer.attribute(ANDROID_NS, "requestLegacyExternalStorage", "true")
        }

        // activities
        for (activity in data.activities) {
            serializer.startTag("", "activity")
            serializer.attribute(ANDROID_NS, "name", activity.name)
            if (activity.label.isNotBlank()) {
                serializer.attribute(ANDROID_NS, "label", activity.label)
            }
            serializer.attribute(ANDROID_NS, "exported", activity.exported.toString())
            if (activity.screenOrientation != "unspecified") {
                serializer.attribute(ANDROID_NS, "screenOrientation", activity.screenOrientation)
            }

            if (activity.isLauncher) {
                serializer.startTag("", "intent-filter")
                serializer.startTag("", "action")
                serializer.attribute(ANDROID_NS, "name", "android.intent.action.MAIN")
                serializer.endTag("", "action")
                serializer.startTag("", "category")
                serializer.attribute(ANDROID_NS, "name", "android.intent.category.LAUNCHER")
                serializer.endTag("", "category")
                serializer.endTag("", "intent-filter")
            }

            serializer.endTag("", "activity")
        }

        serializer.endTag("", "application")
        serializer.endTag("", "manifest")
        serializer.endDocument()

        return writer.toString()
    }

    private class ManifestActivityBuilder(
        val name: String,
        val label: String,
        val exported: Boolean,
        val screenOrientation: String,
        var hasMainAction: Boolean = false,
        var hasLauncherCategory: Boolean = false
    )
}
