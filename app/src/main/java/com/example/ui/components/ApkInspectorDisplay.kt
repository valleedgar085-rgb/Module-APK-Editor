package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.ApkProcessingProgress
import com.example.model.LoadedApkDetails
import com.example.ui.theme.WorkbenchActive
import com.example.ui.theme.WorkbenchBorder
import com.example.ui.theme.WorkbenchCode
import com.example.ui.theme.WorkbenchCyan
import com.example.ui.theme.WorkbenchElevated
import com.example.ui.theme.WorkbenchLabel
import com.example.ui.theme.WorkbenchLime
import com.example.ui.theme.WorkbenchSurface
import com.example.ui.theme.WorkbenchTextPrimary
import com.example.ui.theme.WorkbenchTextSecondary
import com.example.ui.theme.WorkbenchWarning
import com.example.viewmodel.ManifestViewModel

@Composable
fun ApkInspectorDisplay(
    apkDetails: LoadedApkDetails?,
    isLoading: Boolean,
    progress: ApkProcessingProgress,
    onPickApkClick: () -> Unit,
    onLoadIntoEditorClick: () -> Unit,
    viewModel: ManifestViewModel,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        ApkLoadingState(progress = progress, modifier = modifier)
        return
    }

    if (apkDetails == null) {
        ApkEmptyState(
            onPickApkClick = onPickApkClick,
            onLoadSampleClick = viewModel::loadSampleApkDemo,
            modifier = modifier
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("lazy_column_apk_details"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = WorkbenchSurface,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, WorkbenchCyan),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_apk_header")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ApkIconView(
                            bitmap = apkDetails.iconBitmap,
                            appLabel = apkDetails.appLabel,
                            size = 64.dp,
                            modifier = Modifier.testTag("header_apk_icon_thumbnail")
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = apkDetails.appLabel,
                                style = MaterialTheme.typography.titleMedium,
                                color = WorkbenchTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = apkDetails.packageName,
                                style = WorkbenchCode,
                                color = WorkbenchTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = apkDetails.fileSizeFormatted + " · APK",
                                style = WorkbenchLabel,
                                color = WorkbenchLime
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        WorkbenchActionButton(
                            label = "CHANGE APK",
                            onClick = onPickApkClick,
                            primary = false,
                            testTag = "button_change_apk"
                        )
                        WorkbenchActionButton(
                            label = "LOAD INTO EDITOR",
                            onClick = onLoadIntoEditorClick,
                            primary = true,
                            testTag = "button_load_into_editor"
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WorkbenchMetricTile(
                    label = "VERSION",
                    value = apkDetails.versionName,
                    valueColor = WorkbenchCyan
                )
                WorkbenchMetricTile(
                    label = "CODE",
                    value = apkDetails.versionCode.toString()
                )
                WorkbenchMetricTile(
                    label = "SDK",
                    value = apkDetails.minSdk.toString() + " → " + apkDetails.targetSdk.toString(),
                    valueColor = WorkbenchLime
                )
            }
        }

        item {
            WorkbenchSection(
                kicker = "PERMISSIONS · " + apkDetails.permissions.size,
                title = "Manifest permissions",
                accent = WorkbenchWarning
            ) {
                if (apkDetails.permissions.isEmpty()) {
                    Text(
                        text = "No permissions declared in this APK.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WorkbenchTextSecondary
                    )
                } else {
                    apkDetails.permissions.forEach { permission ->
                        val runtime = isRuntimePermission(permission)
                        WorkbenchPermissionRow(
                            name = permission,
                            badge = if (runtime) "RUNTIME" else "NORMAL",
                            badgeColor = if (runtime) WorkbenchWarning else WorkbenchLime,
                            modifier = Modifier.testTag(
                                "item_perm_" + permission.substringAfterLast(".")
                            )
                        )
                    }
                }
            }
        }

        item {
            WorkbenchSection(
                kicker = "ARCHIVE",
                title = "APK package details"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WorkbenchMetricTile(
                        label = "SIZE",
                        value = apkDetails.fileSizeFormatted,
                        valueColor = WorkbenchCyan
                    )
                    WorkbenchMetricTile(
                        label = "DEX",
                        value = apkDetails.totalDexCount.toString()
                    )
                    WorkbenchMetricTile(
                        label = "DEBUG",
                        value = if (apkDetails.isDebuggable) "YES" else "NO",
                        valueColor = if (apkDetails.isDebuggable) WorkbenchWarning else WorkbenchLime
                    )
                }
            }
        }

        item {
            WorkbenchSection(
                kicker = "ACTIVITIES · " + apkDetails.activities.size,
                title = "Registered activities"
            ) {
                if (apkDetails.activities.isEmpty()) {
                    Text(
                        text = "No activities registered.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WorkbenchTextSecondary
                    )
                }
            }
        }

        items(apkDetails.activities) { activity ->
            Surface(
                color = WorkbenchElevated,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, WorkbenchBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("item_act_" + activity.substringAfterLast("."))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = activity.substringAfterLast("."),
                        style = MaterialTheme.typography.titleSmall,
                        color = WorkbenchTextPrimary
                    )
                    Text(
                        text = activity,
                        style = WorkbenchCode,
                        color = WorkbenchTextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ApkLoadingState(
    progress: ApkProcessingProgress,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("container_apk_loading_indicator"),
        contentAlignment = Alignment.Center
    ) {
        WorkbenchSection(
            kicker = "APK / ANALYZE",
            title = "Analyzing APK & Manifest"
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = WorkbenchCyan,
                    trackColor = WorkbenchElevated,
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("circular_progress_indicator_active")
                )
            }
            Text(
                text = progress.stage,
                style = MaterialTheme.typography.titleSmall,
                color = WorkbenchCyan
            )
            LinearProgressIndicator(
                progress = { progress.percentage },
                color = WorkbenchCyan,
                trackColor = WorkbenchElevated,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .testTag("linear_progress_indicator_active")
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "STEP " + progress.currentStep + " / " + progress.totalSteps,
                    style = WorkbenchLabel,
                    color = WorkbenchTextSecondary
                )
                Text(
                    text = (progress.percentage * 100).toInt().toString() + "%",
                    style = WorkbenchLabel,
                    color = WorkbenchCyan
                )
            }
        }
    }
}

@Composable
private fun ApkEmptyState(
    onPickApkClick: () -> Unit,
    onLoadSampleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        WorkbenchSection(
            kicker = "APK / INSPECT",
            title = "Choose an APK to inspect",
            modifier = Modifier.testTag("card_empty_apk_picker")
        ) {
            Text(
                text = "Read package identity, versioning, SDK levels, permissions, activities, archive size, and icon data from an APK.",
                style = MaterialTheme.typography.bodyMedium,
                color = WorkbenchTextSecondary
            )
            WorkbenchActionButton(
                label = "SELECT .APK FILE",
                onClick = onPickApkClick,
                primary = true,
                modifier = Modifier.fillMaxWidth(),
                testTag = "button_pick_apk_file"
            )
            WorkbenchActionButton(
                label = "LOAD SAMPLE APK",
                onClick = onLoadSampleClick,
                primary = false,
                modifier = Modifier.fillMaxWidth(),
                testTag = "button_load_sample_apk"
            )
        }
    }
}

@Composable
fun ApkIconView(
    bitmap: Bitmap?,
    appLabel: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = appLabel + " App Icon",
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(18.dp))
                .border(
                    width = 1.dp,
                    color = WorkbenchCyan,
                    shape = RoundedCornerShape(18.dp)
                ),
            contentScale = ContentScale.Crop
        )
    } else {
        Surface(
            color = WorkbenchActive,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, WorkbenchCyan),
            modifier = modifier.size(size)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = appLabel.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                    style = MaterialTheme.typography.displaySmall,
                    color = WorkbenchCyan
                )
            }
        }
    }
}

private fun isRuntimePermission(permission: String): Boolean {
    val runtimeNames = setOf(
        "POST_NOTIFICATIONS",
        "CAMERA",
        "RECORD_AUDIO",
        "READ_MEDIA_AUDIO",
        "READ_MEDIA_IMAGES",
        "READ_MEDIA_VIDEO",
        "ACCESS_FINE_LOCATION",
        "ACCESS_COARSE_LOCATION",
        "READ_CONTACTS",
        "WRITE_CONTACTS",
        "READ_CALENDAR",
        "WRITE_CALENDAR",
        "CALL_PHONE",
        "READ_PHONE_STATE"
    )
    return permission.substringAfterLast(".") in runtimeNames
}
