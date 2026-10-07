package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddActivityDialog
import com.example.ui.components.AddPermissionDialog
import com.example.ui.components.ApkInspectorDisplay
import com.example.ui.components.ApkProcessingDialog
import com.example.ui.components.DiffViewerScreen
import com.example.ui.components.ImportXmlDialog
import com.example.ui.components.ManifestFormScreen
import com.example.ui.components.PresetsScreen
import com.example.ui.components.SavePresetDialog
import com.example.ui.components.WorkbenchActionButton
import com.example.ui.components.WorkbenchBottomNavigation
import com.example.ui.components.WorkbenchScreenFrame
import com.example.ui.components.WorkbenchStatusBanner
import com.example.ui.components.XmlPreviewScreen
import com.example.ui.theme.WorkbenchCyan
import com.example.ui.theme.WorkbenchError
import com.example.ui.theme.WorkbenchLime
import com.example.ui.theme.WorkbenchWarning
import com.example.viewmodel.ManifestViewModel
import com.example.viewmodel.ScreenTab

@Composable
fun MainScreen(
    viewModel: ManifestViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val formScrollState = rememberScrollState()

    val apkFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onApkFileSelected(it) }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            WorkbenchBottomNavigation(
                currentTab = uiState.currentTab,
                onTabSelected = viewModel::setTab
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        when (uiState.currentTab) {
            ScreenTab.Form -> {
                val statusColor = when {
                    uiState.validation.errors.isNotEmpty() -> WorkbenchError
                    uiState.validation.warnings.isNotEmpty() -> WorkbenchWarning
                    else -> WorkbenchLime
                }
                val statusLabel = when {
                    uiState.validation.errors.isNotEmpty() -> "ERROR"
                    uiState.validation.warnings.isNotEmpty() -> "REVIEW"
                    else -> "VALID"
                }
                WorkbenchScreenFrame(
                    eyebrow = "APK / EDITOR",
                    title = "Manifest Editor",
                    statusLabel = statusLabel,
                    statusColor = statusColor,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(formScrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.End
                            )
                        ) {
                            WorkbenchActionButton(
                                label = "IMPORT XML",
                                onClick = { viewModel.setShowImportDialog(true) },
                                primary = false,
                                testTag = "action_import_xml"
                            )
                            WorkbenchActionButton(
                                label = if (uiState.hasUnsavedChanges) "SAVE CHANGES" else "SAVED",
                                onClick = viewModel::saveChanges,
                                primary = true,
                                enabled = uiState.hasUnsavedChanges,
                                testTag = "action_save_manifest"
                            )
                        }

                        WorkbenchStatusBanner(
                            title = when {
                                uiState.validation.errors.isNotEmpty() -> "Manifest has blocking errors"
                                uiState.validation.warnings.isNotEmpty() -> "Manifest needs review"
                                else -> "Manifest ready to build"
                            },
                            detail = when {
                                uiState.validation.errors.isNotEmpty() ->
                                    uiState.validation.errors.size.toString() + " error(s) · " +
                                        uiState.validation.warnings.size.toString() + " warning(s)"
                                uiState.validation.warnings.isNotEmpty() ->
                                    uiState.validation.warnings.size.toString() + " best-practice warning(s)"
                                else -> "All required manifest checks are passing"
                            },
                            color = statusColor
                        )

                        ManifestFormScreen(
                            manifest = uiState.manifest,
                            viewModel = viewModel
                        )
                    }
                }
            }

            ScreenTab.Inspector -> {
                WorkbenchScreenFrame(
                    eyebrow = "APK / INSPECT",
                    title = "APK Inspector",
                    statusLabel = if (uiState.loadedApkDetails != null) "LOADED" else "READY",
                    statusColor = WorkbenchCyan,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    ApkInspectorDisplay(
                        apkDetails = uiState.loadedApkDetails,
                        isLoading = uiState.isAnalyzingApk,
                        progress = uiState.processingProgress,
                        onPickApkClick = { apkFilePickerLauncher.launch("*/*") },
                        onLoadIntoEditorClick = { viewModel.transferApkDetailsToEditor() },
                        viewModel = viewModel
                    )
                }
            }

            ScreenTab.XmlPreview -> {
                WorkbenchScreenFrame(
                    eyebrow = "APK / REVIEW",
                    title = "XML Preview",
                    statusLabel = "LIVE",
                    statusColor = WorkbenchCyan,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    XmlPreviewScreen(
                        xmlContent = uiState.generatedXmlPreview,
                        viewModel = viewModel
                    )
                }
            }

            ScreenTab.Diff -> {
                WorkbenchScreenFrame(
                    eyebrow = "APK / REVIEW",
                    title = "Review Changes",
                    statusLabel = if (uiState.hasUnsavedChanges) "CHANGED" else "CLEAN",
                    statusColor = if (uiState.hasUnsavedChanges) WorkbenchWarning else WorkbenchLime,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    DiffViewerScreen(
                        current = uiState.manifest,
                        original = uiState.originalManifest,
                        validation = uiState.validation,
                        onShowXml = { viewModel.setTab(ScreenTab.XmlPreview) },
                        onDiscard = viewModel::discardChanges,
                        onSave = viewModel::saveChanges
                    )
                }
            }

            ScreenTab.Presets -> {
                WorkbenchScreenFrame(
                    eyebrow = "APK / PRESETS",
                    title = "Manifest Presets",
                    statusLabel = uiState.presets.size.toString() + " SAVED",
                    statusColor = WorkbenchCyan,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    PresetsScreen(
                        presets = uiState.presets,
                        history = uiState.history,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    if (uiState.isAnalyzingApk) {
        ApkProcessingDialog(progress = uiState.processingProgress)
    }

    if (uiState.showAddPermissionDialog) {
        AddPermissionDialog(
            onDismiss = { viewModel.setShowAddPermissionDialog(false) },
            onAdd = {
                viewModel.addPermission(it)
                viewModel.setShowAddPermissionDialog(false)
            }
        )
    }

    if (uiState.showAddActivityDialog) {
        AddActivityDialog(
            onDismiss = { viewModel.setShowAddActivityDialog(false) },
            onAdd = {
                viewModel.addActivity(it)
                viewModel.setShowAddActivityDialog(false)
            }
        )
    }

    if (uiState.showSavePresetDialog) {
        SavePresetDialog(
            onDismiss = { viewModel.setShowSavePresetDialog(false) },
            onSave = { name, desc ->
                viewModel.saveAsPreset(name, desc)
            }
        )
    }

    if (uiState.showImportDialog) {
        ImportXmlDialog(
            onDismiss = { viewModel.setShowImportDialog(false) },
            onImport = { xml ->
                viewModel.importXml(xml)
            }
        )
    }

    if (uiState.showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowResetConfirmDialog(false) },
            title = { Text("Reset to Defaults?") },
            text = { Text("This will discard all current edits and restore standard AndroidManifest defaults.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToDefaults()
                        viewModel.setShowResetConfirmDialog(false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkbenchError)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowResetConfirmDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}
