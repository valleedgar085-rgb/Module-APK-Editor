package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.ManifestData
import com.example.ui.theme.WorkbenchCyan
import com.example.ui.theme.WorkbenchError
import com.example.ui.theme.WorkbenchLime
import com.example.ui.theme.WorkbenchTextPrimary
import com.example.ui.theme.WorkbenchTextSecondary
import com.example.ui.theme.WorkbenchWarning
import com.example.viewmodel.ManifestViewModel

@Composable
fun ManifestFormScreen(
    manifest: ManifestData,
    viewModel: ManifestViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WorkbenchSection(
            kicker = "01 · IDENTITY",
            title = "App identity & package",
            modifier = Modifier.testTag("section_identity_card")
        ) {
            TechnicalTextField(
                label = "App Label",
                value = manifest.appLabel,
                onValueChange = viewModel::updateAppLabel,
                testTag = "input_app_label"
            )
            TechnicalTextField(
                label = "Package Name",
                value = manifest.packageName,
                onValueChange = viewModel::updatePackageName,
                testTag = "input_package_name"
            )
        }

        WorkbenchSection(
            kicker = "02 · VERSION",
            title = "Version & SDK targets",
            accent = WorkbenchLime,
            modifier = Modifier.testTag("section_versioning_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WorkbenchMetricTile(
                    label = "VERSION",
                    value = manifest.versionName,
                    valueColor = WorkbenchCyan
                )
                WorkbenchMetricTile(
                    label = "CODE",
                    value = manifest.versionCode.toString()
                )
                WorkbenchMetricTile(
                    label = "TARGET SDK",
                    value = manifest.targetSdk.toString(),
                    valueColor = WorkbenchLime
                )
            }

            Text(
                text = "EDIT VALUES",
                style = MaterialTheme.typography.labelLarge,
                color = WorkbenchTextSecondary
            )
            TechnicalTextField(
                label = "Version Name",
                value = manifest.versionName,
                onValueChange = viewModel::updateVersionName,
                testTag = "input_version_name"
            )
            TechnicalTextField(
                label = "Version Code",
                value = manifest.versionCode.toString(),
                onValueChange = { value ->
                    value.toIntOrNull()?.let(viewModel::updateVersionCode)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                testTag = "input_version_code"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                WorkbenchActionButton(
                    label = "− CODE",
                    onClick = viewModel::decrementVersionCode,
                    primary = false,
                    testTag = "button_decrement_version_code"
                )
                WorkbenchActionButton(
                    label = "+ CODE",
                    onClick = viewModel::incrementVersionCode,
                    primary = false,
                    testTag = "button_increment_version_code"
                )
            }
        }

        WorkbenchSection(
            kicker = "03 · PLATFORM",
            title = "SDK & platform levels",
            modifier = Modifier.testTag("section_sdk_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TechnicalTextField(
                    label = "minSdk",
                    value = manifest.minSdk.toString(),
                    onValueChange = { value ->
                        value.toIntOrNull()?.let(viewModel::updateMinSdk)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    testTag = "input_min_sdk",
                    modifier = Modifier.weight(1f)
                )
                TechnicalTextField(
                    label = "targetSdk",
                    value = manifest.targetSdk.toString(),
                    onValueChange = { value ->
                        value.toIntOrNull()?.let(viewModel::updateTargetSdk)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    testTag = "input_target_sdk",
                    modifier = Modifier.weight(1f)
                )
                TechnicalTextField(
                    label = "compileSdk",
                    value = manifest.compileSdk.toString(),
                    onValueChange = { value ->
                        value.toIntOrNull()?.let(viewModel::updateCompileSdk)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    testTag = "input_compile_sdk",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WorkbenchActionButton(
                    label = "MODERN 24–36",
                    onClick = {
                        viewModel.updateMinSdk(24)
                        viewModel.updateTargetSdk(36)
                        viewModel.updateCompileSdk(36)
                    },
                    primary = false,
                    modifier = Modifier.weight(1f),
                    testTag = "chip_modern_sdk"
                )
                WorkbenchActionButton(
                    label = "LEGACY 21–34",
                    onClick = {
                        viewModel.updateMinSdk(21)
                        viewModel.updateTargetSdk(34)
                        viewModel.updateCompileSdk(34)
                    },
                    primary = false,
                    modifier = Modifier.weight(1f),
                    testTag = "chip_legacy_sdk"
                )
            }
        }

        WorkbenchSection(
            kicker = "04 · SECURITY",
            title = "App features & security flags",
            accent = WorkbenchWarning,
            modifier = Modifier.testTag("section_app_config_card")
        ) {
            WorkbenchSettingToggle(
                title = "Debuggable",
                subtitle = "Allows debugger attachment. Keep off for release builds.",
                checked = manifest.isDebuggable,
                onCheckedChange = viewModel::toggleDebuggable,
                testTag = "switch_debuggable"
            )
            WorkbenchSettingToggle(
                title = "Allow backup",
                subtitle = "Enables cloud and adb device backups.",
                checked = manifest.allowBackup,
                onCheckedChange = viewModel::toggleAllowBackup,
                testTag = "switch_allow_backup"
            )
            WorkbenchSettingToggle(
                title = "Supports RTL",
                subtitle = "Enables right-to-left layout direction.",
                checked = manifest.supportsRtl,
                onCheckedChange = viewModel::toggleSupportsRtl,
                testTag = "switch_supports_rtl"
            )
            WorkbenchSettingToggle(
                title = "Cleartext traffic",
                subtitle = "Allows unencrypted HTTP network communication.",
                checked = manifest.usesCleartextTraffic,
                onCheckedChange = viewModel::toggleUsesCleartextTraffic,
                testTag = "switch_cleartext_traffic"
            )
            WorkbenchSettingToggle(
                title = "Legacy external storage",
                subtitle = "Deprecated storage compatibility mode.",
                checked = manifest.requestLegacyExternalStorage,
                onCheckedChange = viewModel::toggleRequestLegacyExternalStorage,
                testTag = "switch_legacy_storage"
            )
        }

        WorkbenchSection(
            kicker = "05 · RESOURCES",
            title = "Icons & app theme",
            modifier = Modifier.testTag("section_look_card")
        ) {
            TechnicalTextField(
                label = "Application Theme",
                value = manifest.theme,
                onValueChange = viewModel::updateTheme,
                testTag = "input_theme"
            )
            TechnicalTextField(
                label = "Icon",
                value = manifest.iconDrawable,
                onValueChange = viewModel::updateIcon,
                testTag = "input_icon"
            )
            TechnicalTextField(
                label = "Round Icon",
                value = manifest.roundIconDrawable,
                onValueChange = viewModel::updateRoundIcon,
                testTag = "input_round_icon"
            )
        }

        WorkbenchSection(
            kicker = "PERMISSIONS · " + manifest.permissions.size,
            title = "Manifest permissions",
            accent = WorkbenchWarning,
            modifier = Modifier.testTag("section_permissions_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                WorkbenchActionButton(
                    label = "+ ADD PERMISSION",
                    onClick = { viewModel.setShowAddPermissionDialog(true) },
                    primary = false,
                    testTag = "button_add_permission"
                )
            }

            if (manifest.permissions.isEmpty()) {
                Text(
                    text = "No permissions requested. Clean sandbox mode.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WorkbenchTextSecondary
                )
            } else {
                manifest.permissions.forEach { permission ->
                    val shortName = permission.substringAfterLast(".")
                    WorkbenchPermissionRow(
                        name = permission,
                        badge = if (isRuntimePermission(permission)) "RUNTIME" else "NORMAL",
                        badgeColor = if (isRuntimePermission(permission)) WorkbenchWarning else WorkbenchLime,
                        trailing = {
                            IconButton(
                                onClick = { viewModel.removePermission(permission) },
                                modifier = Modifier.testTag("button_remove_perm_" + shortName)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Remove permission",
                                    tint = WorkbenchError
                                )
                            }
                        }
                    )
                }
            }
        }

        WorkbenchSection(
            kicker = "ACTIVITIES · " + manifest.activities.size,
            title = "Registered activities",
            modifier = Modifier.testTag("section_activities_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                WorkbenchActionButton(
                    label = "+ ADD ACTIVITY",
                    onClick = { viewModel.setShowAddActivityDialog(true) },
                    primary = false,
                    testTag = "button_add_activity"
                )
            }

            manifest.activities.forEachIndexed { index, activity ->
                WorkbenchSection(
                    kicker = if (activity.isLauncher) "LAUNCHER" else "ACTIVITY",
                    title = activity.name,
                    accent = if (activity.isLauncher) WorkbenchLime else WorkbenchCyan,
                    elevated = true
                ) {
                    if (activity.label.isNotBlank()) {
                        Text(
                            text = activity.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = WorkbenchTextSecondary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WorkbenchSettingToggle(
                                title = "Launcher",
                                subtitle = "MAIN / LAUNCHER entry point",
                                checked = activity.isLauncher,
                                onCheckedChange = {
                                    viewModel.updateActivity(index, activity.copy(isLauncher = it))
                                },
                                testTag = "switch_launcher_" + index
                            )
                            WorkbenchSettingToggle(
                                title = "Exported",
                                subtitle = "Visible to other applications",
                                checked = activity.exported,
                                onCheckedChange = {
                                    viewModel.updateActivity(index, activity.copy(exported = it))
                                },
                                testTag = "switch_exported_" + index
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.removeActivity(index) },
                            modifier = Modifier.testTag("button_delete_act_" + index)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete activity",
                                tint = WorkbenchError
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            WorkbenchActionButton(
                label = "RESET TO DEFAULTS",
                onClick = { viewModel.setShowResetConfirmDialog(true) },
                primary = false,
                testTag = "action_reset_default"
            )
        }
    }
}

@Composable
private fun WorkbenchSettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = WorkbenchTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = WorkbenchTextSecondary
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(testTag)
            )
        }
        HorizontalDivider(color = com.example.ui.theme.WorkbenchBorder)
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
