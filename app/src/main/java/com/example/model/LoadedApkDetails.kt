package com.example.model

import android.graphics.Bitmap

/**
 * Metadata and manifest details extracted from an inspected APK file.
 */
data class LoadedApkDetails(
    val fileName: String,
    val fileSizeFormatted: String,
    val fileSizeBytes: Long,
    val packageName: String,
    val versionCode: Int,
    val versionName: String,
    val appLabel: String,
    val minSdk: Int,
    val targetSdk: Int,
    val compileSdk: Int,
    val isDebuggable: Boolean,
    val permissions: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val services: List<String> = emptyList(),
    val receivers: List<String> = emptyList(),
    val totalDexCount: Int = 1,
    val hasRawXml: Boolean = false,
    val rawXml: String = "",
    val iconBitmap: Bitmap? = null
)

/**
 * Real-time progression state when processing an APK archive
 */
data class ApkProcessingProgress(
    val stage: String = "Reading archive...",
    val currentStep: Int = 1,
    val totalSteps: Int = 4,
    val percentage: Float = 0.25f
)
