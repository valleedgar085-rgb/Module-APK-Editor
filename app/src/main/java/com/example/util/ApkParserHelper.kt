package com.example.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import com.example.model.ApkProcessingProgress
import com.example.model.LoadedApkDetails
import com.example.model.ManifestActivity
import com.example.model.ManifestData
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.zip.ZipInputStream

object ApkParserHelper {

    /**
     * Inspects an APK file selected by the user via Uri and extracts AndroidManifest.xml details including app icon,
     * emitting processing progress checkpoints along the way.
     */
    fun parseApkFromUri(
        context: Context,
        uri: Uri,
        onProgress: ((ApkProcessingProgress) -> Unit)? = null
    ): Result<Pair<LoadedApkDetails, ManifestData>> {
        return try {
            onProgress?.invoke(
                ApkProcessingProgress(
                    stage = "Reading file metadata & copying APK...",
                    currentStep = 1,
                    totalSteps = 4,
                    percentage = 0.25f
                )
            )

            val contentResolver = context.contentResolver

            // 1. Get file name & size from ContentResolver query
            var fileName = "application.apk"
            var fileSizeBytes: Long = 0

            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex) ?: fileName
                    }
                    if (sizeIndex != -1) {
                        fileSizeBytes = cursor.getLong(sizeIndex)
                    }
                }
            }

            // 2. Copy the APK stream to a temporary cached file for inspection
            val tempApkFile = File(context.cacheDir, "temp_inspect_${System.currentTimeMillis()}.apk")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempApkFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return Result.failure(Exception("Cannot open stream for selected file"))

            if (fileSizeBytes == 0L) {
                fileSizeBytes = tempApkFile.length()
            }

            onProgress?.invoke(
                ApkProcessingProgress(
                    stage = "Analyzing ZIP archive & DEX entries...",
                    currentStep = 2,
                    totalSteps = 4,
                    percentage = 0.50f
                )
            )

            // 3. Inspect DEX count and zip entries
            var dexCount = 0
            var hasBinaryManifest = false
            try {
                ZipInputStream(tempApkFile.inputStream()).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        if (entry.name.endsWith(".dex", ignoreCase = true)) {
                            dexCount++
                        }
                        if (entry.name.equals("AndroidManifest.xml", ignoreCase = true)) {
                            hasBinaryManifest = true
                        }
                        entry = zip.nextEntry
                    }
                }
            } catch (_: Exception) {}

            onProgress?.invoke(
                ApkProcessingProgress(
                    stage = "Extracting AndroidManifest.xml package details...",
                    currentStep = 3,
                    totalSteps = 4,
                    percentage = 0.75f
                )
            )

            // 4. Use Android's PackageManager archive parser to inspect the APK package info & icon
            val pm = context.packageManager
            val flags = PackageManager.GET_ACTIVITIES or
                    PackageManager.GET_PERMISSIONS or
                    PackageManager.GET_SERVICES or
                    PackageManager.GET_RECEIVERS

            val packageInfo = pm.getPackageArchiveInfo(tempApkFile.absolutePath, flags)

            onProgress?.invoke(
                ApkProcessingProgress(
                    stage = "Extracting launcher app icon & finalizing...",
                    currentStep = 4,
                    totalSteps = 4,
                    percentage = 0.95f
                )
            )

            val parsedDetails: LoadedApkDetails
            val manifestData: ManifestData

            if (packageInfo != null) {
                val appInfo = packageInfo.applicationInfo
                appInfo?.sourceDir = tempApkFile.absolutePath
                appInfo?.publicSourceDir = tempApkFile.absolutePath

                val packageName = packageInfo.packageName ?: "com.unknown.app"
                val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode.toInt()
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode
                }
                val versionName = packageInfo.versionName ?: "1.0"
                val appLabel = appInfo?.loadLabel(pm)?.toString()
                    ?: (appInfo?.nonLocalizedLabel?.toString() ?: fileName.removeSuffix(".apk"))

                // Extract Icon Bitmap
                var extractedIcon: Bitmap? = null
                try {
                    val iconDrawable = appInfo?.loadIcon(pm)
                    if (iconDrawable != null) {
                        extractedIcon = drawableToBitmap(iconDrawable)
                    }
                } catch (_: Exception) {}

                val minSdk = appInfo?.minSdkVersion ?: 21
                val targetSdk = appInfo?.targetSdkVersion ?: 34
                val isDebuggable = (appInfo?.flags?.and(android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) ?: 0) != 0

                val permissionsList = packageInfo.requestedPermissions?.toList() ?: emptyList()
                val activitiesList = packageInfo.activities?.map { it.name } ?: emptyList()
                val servicesList = packageInfo.services?.map { it.name } ?: emptyList()
                val receiversList = packageInfo.receivers?.map { it.name } ?: emptyList()

                val manifestActivities = packageInfo.activities?.mapIndexed { index, act ->
                    ManifestActivity(
                        name = act.name,
                        label = act.loadLabel(pm).toString(),
                        exported = act.exported,
                        isLauncher = index == 0
                    )
                } ?: listOf(
                    ManifestActivity(
                        name = ".MainActivity",
                        label = appLabel,
                        exported = true,
                        isLauncher = true
                    )
                )

                parsedDetails = LoadedApkDetails(
                    fileName = fileName,
                    fileSizeFormatted = formatFileSize(fileSizeBytes),
                    fileSizeBytes = fileSizeBytes,
                    packageName = packageName,
                    versionCode = versionCode,
                    versionName = versionName,
                    appLabel = appLabel,
                    minSdk = minSdk,
                    targetSdk = targetSdk,
                    compileSdk = targetSdk,
                    isDebuggable = isDebuggable,
                    permissions = permissionsList,
                    activities = activitiesList,
                    services = servicesList,
                    receivers = receiversList,
                    totalDexCount = if (dexCount > 0) dexCount else 1,
                    hasRawXml = hasBinaryManifest,
                    iconBitmap = extractedIcon
                )

                manifestData = ManifestData(
                    packageName = packageName,
                    versionCode = versionCode,
                    versionName = versionName,
                    appLabel = appLabel,
                    minSdk = minSdk,
                    targetSdk = targetSdk,
                    compileSdk = targetSdk,
                    isDebuggable = isDebuggable,
                    permissions = permissionsList,
                    activities = manifestActivities
                )
            } else {
                // Fallback if PackageArchiveInfo returns null
                val fallbackPkg = fileName.removeSuffix(".apk").lowercase().replace(Regex("[^a-z0-9]"), ".")
                parsedDetails = LoadedApkDetails(
                    fileName = fileName,
                    fileSizeFormatted = formatFileSize(fileSizeBytes),
                    fileSizeBytes = fileSizeBytes,
                    packageName = if (fallbackPkg.contains(".")) fallbackPkg else "com.example.$fallbackPkg",
                    versionCode = 1,
                    versionName = "1.0.0",
                    appLabel = fileName.removeSuffix(".apk"),
                    minSdk = 24,
                    targetSdk = 34,
                    compileSdk = 34,
                    isDebuggable = false,
                    totalDexCount = if (dexCount > 0) dexCount else 1,
                    hasRawXml = hasBinaryManifest,
                    iconBitmap = null
                )

                manifestData = ManifestData(
                    packageName = parsedDetails.packageName,
                    versionCode = parsedDetails.versionCode,
                    versionName = parsedDetails.versionName,
                    appLabel = parsedDetails.appLabel,
                    minSdk = parsedDetails.minSdk,
                    targetSdk = parsedDetails.targetSdk,
                    compileSdk = parsedDetails.compileSdk
                )
            }

            // Cleanup temp file
            try {
                tempApkFile.delete()
            } catch (_: Exception) {}

            Result.success(Pair(parsedDetails, manifestData))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Converts a Drawable (such as AdaptiveIconDrawable, BitmapDrawable, VectorDrawable) into a clean software Bitmap.
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 192
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 192

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        val format = DecimalFormat("#,##0.#")
        return "${format.format(bytes / Math.pow(1024.0, digitGroups.toDouble()))} ${units[digitGroups]}"
    }
}
