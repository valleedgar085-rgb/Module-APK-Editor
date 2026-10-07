package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.ManifestActivity
import com.example.model.ManifestData
import com.example.viewmodel.ManifestViewModel

@Composable
fun ManifestFormScreen(
    manifest: ManifestData,
    viewModel: ManifestViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Identity Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("section_identity_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    icon = Icons.Default.Badge,
                    title = "App Identity & Package",
                    subtitle = "Package namespace and launcher title"
                )

                OutlinedTextField(
                    value = manifest.appLabel,
                    onValueChange = { viewModel.updateAppLabel(it) },
                    label = { Text("App Label (android:label)") },
                    placeholder = { Text("My Application") },
                    leadingIcon = { Icon(Icons.Default.Title, contentDescription = "App Label") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_app_label")
                )

                OutlinedTextField(
                    value = manifest.packageName,
                    onValueChange = { viewModel.updatePackageName(it) },
                    label = { Text("Package Name (package)") },
                    placeholder = { Text("com.example.myapp") },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = "Package Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_package_name")
                )
            }
        }

        // 2. Versioning Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("section_versioning_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    icon = Icons.Default.Tag,
                    title = "Versioning",
                    subtitle = "Version code & human-readable version string"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manifest.versionCode.toString(),
                        onValueChange = {
                            val v = it.toIntOrNull() ?: 1
                            viewModel.updateVersionCode(v)
                        },
                        label = { Text("Version Code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_version_code")
                    )

                    IconButton(
                        onClick = { viewModel.decrementVersionCode() },
                        modifier = Modifier.testTag("button_decrement_version_code")
                    ) {
                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease Version Code")
                    }

                    IconButton(
                        onClick = { viewModel.incrementVersionCode() },
                        modifier = Modifier.testTag("button_increment_version_code")
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase Version Code")
                    }
                }

                OutlinedTextField(
                    value = manifest.versionName,
                    onValueChange = { viewModel.updateVersionName(it) },
                    label = { Text("Version Name (android:versionName)") },
                    placeholder = { Text("1.0.0") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Version Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_version_name")
                )
            }
        }

        // 3. SDK & Compatibility Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("section_sdk_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    icon = Icons.Default.Devices,
                    title = "SDK & Platform Levels",
                    subtitle = "Target, compile, and minimum API levels"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = manifest.minSdk.toString(),
                        onValueChange = {
                            val v = it.toIntOrNull() ?: 21
                            viewModel.updateMinSdk(v)
                        },
                        label = { Text("minSdk") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_min_sdk")
                    )

                    OutlinedTextField(
                        value = manifest.targetSdk.toString(),
                        onValueChange = {
                            val v = it.toIntOrNull() ?: 34
                            viewModel.updateTargetSdk(v)
                        },
                        label = { Text("targetSdk") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_target_sdk")
                    )

                    OutlinedTextField(
                        value = manifest.compileSdk.toString(),
                        onValueChange = {
                            val v = it.toIntOrNull() ?: 34
                            viewModel.updateCompileSdk(v)
                        },
                        label = { Text("compileSdk") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_compile_sdk")
                    )
                }

                // Quick selector buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {
                            viewModel.updateMinSdk(24)
                            viewModel.updateTargetSdk(36)
                            viewModel.updateCompileSdk(36)
                        },
                        label = { Text("Modern (API 24-36)") },
                        modifier = Modifier.testTag("chip_modern_sdk")
                    )
                    AssistChip(
                        onClick = {
                            viewModel.updateMinSdk(21)
                            viewModel.updateTargetSdk(34)
                            viewModel.updateCompileSdk(34)
                        },
                        label = { Text("Legacy (API 21-34)") },
                        modifier = Modifier.testTag("chip_legacy_sdk")
                    )
                }
            }
        }

        // 4. Application Configuration & Security Flags
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("section_app_config_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader(
                    icon = Icons.Default.Security,
                    title = "App Features & Security Flags",
                    subtitle = "Backup, debug, RTL and network traffic settings"
                )

                SettingToggleRow(
                    title = "Debuggable (android:debuggable)",
                    subtitle = "Allows attach debugger. Recommended false for release.",
                    checked = manifest.isDebuggable,
                    onCheckedChange = { viewModel.toggleDebuggable(it) },
                    testTag = "switch_debuggable"
                )
                HorizontalDivider()

                SettingToggleRow(
                    title = "Allow Backup (android:allowBackup)",
                    subtitle = "Enables cloud and adb device backups.",
                    checked = manifest.allowBackup,
                    onCheckedChange = { viewModel.toggleAllowBackup(it) },
                    testTag = "switch_allow_backup"
                )
                HorizontalDivider()

                SettingToggleRow(
                    title = "Supports RTL (android:supportsRtl)",
                    subtitle = "Enables right-to-left layout direction.",
                    checked = manifest.supportsRtl,
                    onCheckedChange = { viewModel.toggleSupportsRtl(it) },
                    testTag = "switch_supports_rtl"
                )
                HorizontalDivider()

                SettingToggleRow(
                    title = "Cleartext Traffic (usesCleartextTraffic)",
                    subtitle = "Permits unencrypted HTTP network communication.",
                    checked = manifest.usesCleartextTraffic,
                    onCheckedChange = { viewModel.toggleUsesCleartextTraffic(it) },
                    testTag = "switch_cleartext_traffic"
                )
                HorizontalDivider()

                SettingToggleRow(
                    title = "Legacy External Storage",
                    subtitle = "Deprecated storage model (API <= 29 compatibility).",
                    checked = manifest.requestLegacyExternalStorage,
                    onCheckedChange = { viewModel.toggleRequestLegacyExternalStorage(it) },
                    testTag = "switch_legacy_storage"
                )
            }
        }

        // 5. Look & Theme Resources
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("section_look_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    icon = Icons.Default.Palette,
                    title = "Icons & App Theme",
                    subtitle = "Drawable and theme resource references"
                )

                OutlinedTextField(
                    value = manifest.theme,
                    onValueChange = { viewModel.updateTheme(it) },
                    label = { Text("Application Theme (android:theme)") },
                    placeholder = { Text("@style/Theme.MyApplication") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_theme")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = manifest.iconDrawable,
                        onValueChange = { viewModel.updateIcon(it) },
                        label = { Text("Icon") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_icon")
                    )

                    OutlinedTextField(
                        value = manifest.roundIconDrawable,
                        onValueChange = { viewModel.updateRoundIcon(it) },
                        label = { Text("Round Icon") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_round_icon")
                    )
                }
            }
        }

        // 6. Permissions Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("section_permissions_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(
                        icon = Icons.Default.Lock,
                        title = "Permissions (${manifest.permissions.size})",
                        subtitle = "uses-permission tags"
                    )

                    FilledTonalButton(
                        onClick = { viewModel.setShowAddPermissionDialog(true) },
                        modifier = Modifier.testTag("button_add_permission")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Permission")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add")
                    }
                }

                if (manifest.permissions.isEmpty()) {
                    Text(
                        text = "No permissions requested. Clean sandbox mode.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    manifest.permissions.forEach { perm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val shortName = perm.substringAfterLast(".")
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = shortName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = perm,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { viewModel.removePermission(perm) },
                                modifier = Modifier.testTag("button_remove_perm_$shortName")
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Remove $perm",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }

        // 7. Activities Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("section_activities_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(
                        icon = Icons.Default.ViewCarousel,
                        title = "Activities (${manifest.activities.size})",
                        subtitle = "Configured application activities"
                    )

                    FilledTonalButton(
                        onClick = { viewModel.setShowAddActivityDialog(true) },
                        modifier = Modifier.testTag("button_add_activity")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Activity")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add")
                    }
                }

                manifest.activities.forEachIndexed { index, act ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = act.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { viewModel.removeActivity(index) },
                                    modifier = Modifier.testTag("button_delete_act_$index")
                                ) {
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = "Delete Activity",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            if (act.label.isNotBlank()) {
                                Text("Label: ${act.label}", style = MaterialTheme.typography.bodySmall)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = act.isLauncher,
                                    onClick = {
                                        viewModel.updateActivity(index, act.copy(isLauncher = !act.isLauncher))
                                    },
                                    label = { Text(if (act.isLauncher) "Launcher Activity" else "Normal") },
                                    leadingIcon = {
                                        if (act.isLauncher) Icon(Icons.Default.Star, contentDescription = null)
                                    }
                                )

                                FilterChip(
                                    selected = act.exported,
                                    onClick = {
                                        viewModel.updateActivity(index, act.copy(exported = !act.exported))
                                    },
                                    label = { Text("Exported: ${act.exported}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilledIconButton(
            onClick = {},
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(imageVector = icon, contentDescription = title)
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}
