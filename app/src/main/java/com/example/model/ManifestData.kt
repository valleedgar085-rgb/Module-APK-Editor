package com.example.model

/**
 * Represents structured fields of an AndroidManifest.xml
 */
data class ManifestData(
    val packageName: String = "com.example.app",
    val versionCode: Int = 1,
    val versionName: String = "1.0.0",
    val appLabel: String = "My Application",
    val minSdk: Int = 24,
    val targetSdk: Int = 34,
    val compileSdk: Int = 34,
    val isDebuggable: Boolean = false,
    val allowBackup: Boolean = true,
    val supportsRtl: Boolean = true,
    val usesCleartextTraffic: Boolean = false,
    val requestLegacyExternalStorage: Boolean = false,
    val iconDrawable: String = "@mipmap/ic_launcher",
    val roundIconDrawable: String = "@mipmap/ic_launcher_round",
    val theme: String = "@style/Theme.MyApplication",
    val permissions: List<String> = listOf(
        "android.permission.INTERNET",
        "android.permission.ACCESS_NETWORK_STATE"
    ),
    val activities: List<ManifestActivity> = listOf(
        ManifestActivity(
            name = ".MainActivity",
            label = "@string/app_name",
            exported = true,
            isLauncher = true,
            screenOrientation = "unspecified"
        )
    ),
    val rawXml: String = ""
)

data class ManifestActivity(
    val name: String,
    val label: String = "",
    val exported: Boolean = false,
    val isLauncher: Boolean = false,
    val screenOrientation: String = "unspecified"
)
