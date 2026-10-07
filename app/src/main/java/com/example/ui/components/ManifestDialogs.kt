package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ManifestActivity

@Composable
fun AddPermissionDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    val commonPermissions = listOf(
        "android.permission.INTERNET",
        "android.permission.ACCESS_NETWORK_STATE",
        "android.permission.POST_NOTIFICATIONS",
        "android.permission.CAMERA",
        "android.permission.RECORD_AUDIO",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.VIBRATE",
        "android.permission.WAKE_LOCK",
        "android.permission.FOREGROUND_SERVICE"
    )

    var customPermission by remember { mutableStateOf("") }
    var selectedCommon by remember { mutableStateOf(commonPermissions.first()) }
    var isCustom by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Android Permission") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Select from standard Android permissions or type a custom permission string.",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isCustom,
                        onClick = { isCustom = false },
                        label = { Text("Common") }
                    )
                    FilterChip(
                        selected = isCustom,
                        onClick = { isCustom = true },
                        label = { Text("Custom") }
                    )
                }

                if (!isCustom) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        commonPermissions.take(6).forEach { perm ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                RadioButton(
                                    selected = selectedCommon == perm,
                                    onClick = { selectedCommon = perm }
                                )
                                Text(
                                    text = perm.substringAfterLast("."),
                                    modifier = Modifier.padding(top = 12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = customPermission,
                        onValueChange = { customPermission = it },
                        label = { Text("Permission name") },
                        placeholder = { Text("com.example.CUSTOM_PERMISSION") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_custom_permission")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalPerm = if (isCustom) customPermission else selectedCommon
                    if (finalPerm.isNotBlank()) {
                        onAdd(finalPerm)
                    }
                },
                modifier = Modifier.testTag("button_dialog_confirm_add_perm")
            ) {
                Text("Add Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onAdd: (ManifestActivity) -> Unit
) {
    var name by remember { mutableStateOf(".NewActivity") }
    var label by remember { mutableStateOf("") }
    var exported by remember { mutableStateOf(false) }
    var isLauncher by remember { mutableStateOf(false) }
    var orientation by remember { mutableStateOf("unspecified") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Activity") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Activity class (android:name)") },
                    placeholder = { Text(".DetailActivity") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_new_activity_name")
                )

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Exported", modifier = Modifier.padding(top = 8.dp))
                    Switch(checked = exported, onCheckedChange = { exported = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Set as Launcher", modifier = Modifier.padding(top = 8.dp))
                    Switch(
                        checked = isLauncher,
                        onCheckedChange = {
                            isLauncher = it
                            if (it) exported = true // launcher must be exported
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            ManifestActivity(
                                name = name.trim(),
                                label = label.trim(),
                                exported = exported,
                                isLauncher = isLauncher,
                                screenOrientation = orientation
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("button_dialog_confirm_add_act")
            ) {
                Text("Add Activity")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SavePresetDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, desc: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Current as Preset") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Preset Name") },
                    placeholder = { Text("e.g. Production Candidate") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_preset_name")
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    placeholder = { Text("e.g. Target SDK 35 with camera enabled") },
                    modifier = Modifier.fillMaxWidth().testTag("input_preset_desc")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim(), desc.trim())
                    }
                },
                modifier = Modifier.testTag("button_dialog_confirm_save_preset")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ImportXmlDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var xmlText by remember {
        mutableStateOf(
            """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.importedapp"
    android:versionCode="2"
    android:versionName="1.1.0">
    <uses-sdk android:minSdkVersion="24" android:targetSdkVersion="35" />
    <uses-permission android:name="android.permission.INTERNET" />
    <application
        android:label="Imported App"
        android:icon="@mipmap/ic_launcher"
        android:allowBackup="true"
        android:theme="@style/Theme.MyApplication">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>""".trimIndent()
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Manifest XML") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Paste raw AndroidManifest.xml source to parse and populate the editor.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = xmlText,
                    onValueChange = { xmlText = it },
                    label = { Text("Raw XML") },
                    maxLines = 10,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("input_import_xml")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (xmlText.isNotBlank()) {
                        onImport(xmlText)
                    }
                },
                modifier = Modifier.testTag("button_dialog_confirm_import")
            ) {
                Text("Parse & Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
