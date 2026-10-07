package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.ApkProcessingProgress
import com.example.model.LoadedApkDetails
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
        // High-fidelity centered loading display featuring circular progress indicator
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp)
                .testTag("container_apk_loading_indicator"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_loading_progress"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        modifier = Modifier.size(76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("circular_progress_indicator_active"),
                            strokeWidth = 6.dp,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                        Icon(
                            imageVector = Icons.Default.FolderZip,
                            contentDescription = "APK Archive",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Analyzing APK & Manifest",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = progress.stage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress.percentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .testTag("linear_progress_indicator_active"),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Step ${progress.currentStep} of ${progress.totalSteps}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "${(progress.percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = "Extracting AndroidManifest.xml package attributes, versioning, permissions, and app icon...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        return
    }

    if (apkDetails == null) {
        // Empty state with friendly prompt and prominent picker button
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_empty_apk_picker"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderZip,
                            contentDescription = "Select APK",
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "Inspect APK AndroidManifest.xml",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Select any .apk installation package from your device to extract its app icon, package name, version code, version name, permissions, and activities.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = onPickApkClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("button_pick_apk_file"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.FileOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select .apk File", style = MaterialTheme.typography.labelLarge)
                    }

                    // Demo sample loader
                    OutlinedButton(
                        onClick = { viewModel.loadSampleApkDemo() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_load_sample_apk"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Load Sample APK Preview")
                    }
                }
            }
        }
        return
    }

    // LazyColumn displaying parsed AndroidManifest.xml details & extracted App Icon
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("lazy_column_apk_details"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Banner & Actions with App Icon
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_apk_header"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Extracted App Icon Thumbnail
                            ApkIconView(
                                bitmap = apkDetails.iconBitmap,
                                appLabel = apkDetails.appLabel,
                                size = 56.dp,
                                modifier = Modifier.testTag("header_apk_icon_thumbnail")
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = apkDetails.appLabel,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = apkDetails.fileName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        AssistChip(
                            onClick = onPickApkClick,
                            label = { Text("Change") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.FileOpen,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.testTag("button_change_apk")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onLoadIntoEditorClick,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_import_apk_to_editor"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit in Form")
                        }
                    }
                }
            }
        }

        // 2. Extracted App Icon Spotlight Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_detail_app_icon"),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ApkIconView(
                        bitmap = apkDetails.iconBitmap,
                        appLabel = apkDetails.appLabel,
                        size = 64.dp,
                        modifier = Modifier.testTag("display_extracted_app_icon")
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Extracted App Icon",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            AssistChip(
                                onClick = {},
                                label = { Text(if (apkDetails.iconBitmap != null) "PNG / Adaptive" else "Default Icon", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.height(24.dp)
                            )
                        }

                        Text(
                            text = if (apkDetails.iconBitmap != null) {
                                "${apkDetails.iconBitmap.width} × ${apkDetails.iconBitmap.height} px • Extracted from APK resources"
                            } else {
                                "Standard fallback icon extracted from manifest resource"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "<application android:icon>",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // 3. Primary Manifest Key Fields (Package, Version Code, Version Name)
        item {
            Text(
                text = "Key Manifest Attributes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        // Package Name Card
        item {
            ApkDetailItemCard(
                icon = Icons.Default.Inventory2,
                title = "Package Name",
                xmlAttribute = "manifest.package",
                value = apkDetails.packageName,
                tag = "card_detail_package_name"
            )
        }

        // Version Code Card
        item {
            ApkDetailItemCard(
                icon = Icons.Default.Tag,
                title = "Version Code",
                xmlAttribute = "android:versionCode",
                value = apkDetails.versionCode.toString(),
                tag = "card_detail_version_code",
                badge = "Integer Build Number"
            )
        }

        // Version Name Card
        item {
            ApkDetailItemCard(
                icon = Icons.Default.Info,
                title = "Version Name",
                xmlAttribute = "android:versionName",
                value = apkDetails.versionName,
                tag = "card_detail_version_name",
                badge = "Display Version"
            )
        }

        // App Label Card
        item {
            ApkDetailItemCard(
                icon = Icons.Default.Title,
                title = "Application Label",
                xmlAttribute = "android:label",
                value = apkDetails.appLabel,
                tag = "card_detail_app_label"
            )
        }

        // 4. Platform & Target SDK Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_detail_sdk_levels"),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SDK & Platform Compatibility",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SdkBadge(label = "minSdkVersion", value = apkDetails.minSdk.toString())
                        SdkBadge(label = "targetSdkVersion", value = apkDetails.targetSdk.toString())
                        SdkBadge(label = "compileSdkVersion", value = apkDetails.compileSdk.toString())
                    }
                }
            }
        }

        // 5. Archive Statistics (File size & DEX count)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_detail_archive_stats"),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("File Size", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(apkDetails.fileSizeFormatted, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DEX Files", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${apkDetails.totalDexCount} classes.dex", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Debuggable", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(if (apkDetails.isDebuggable) "YES" else "NO", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 6. Declared Permissions
        item {
            Text(
                text = "Declared Permissions (${apkDetails.permissions.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        if (apkDetails.permissions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "No permissions declared in this APK.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(apkDetails.permissions) { perm ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_perm_${perm.substringAfterLast(".")}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = perm.substringAfterLast("."),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = perm,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 7. Registered Activities
        item {
            Text(
                text = "Registered Activities (${apkDetails.activities.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        if (apkDetails.activities.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "No activities registered.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(apkDetails.activities) { act ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_act_${act.substringAfterLast(".")}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewCarousel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = act.substringAfterLast("."),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = act,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Renders the extracted APK app icon bitmap if available, or a graceful high-res styled vector avatar.
 */
@Composable
fun ApkIconView(
    bitmap: Bitmap?,
    appLabel: String,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "$appLabel App Icon",
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.22f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(size * 0.22f)
                ),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.22f))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Android,
                contentDescription = "$appLabel App Icon Placeholder",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}

@Composable
private fun ApkDetailItemCard(
    icon: ImageVector,
    title: String,
    xmlAttribute: String,
    value: String,
    tag: String,
    badge: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilledIconButton(
                onClick = {},
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(imageVector = icon, contentDescription = title)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    badge?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text(it, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = if (value.contains(".")) FontFamily.Monospace else FontFamily.Default
                )

                Text(
                    text = "<manifest $xmlAttribute>",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun SdkBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
