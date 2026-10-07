package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.viewmodel.ManifestViewModel
import com.example.viewmodel.ScreenTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ManifestViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val formScrollState = rememberScrollState()

    // File picker UI component using ActivityResultContracts.GetContent
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
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "APK Easy Edit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AndroidManifest.xml Editor & Inspector",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Open .apk file picker button in TopAppBar
                    IconButton(
                        onClick = { apkFilePickerLauncher.launch("*/*") },
                        modifier = Modifier.testTag("action_pick_apk_file")
                    ) {
                        Icon(Icons.Default.FolderZip, contentDescription = "Pick .apk file")
                    }

                    // Import XML action
                    IconButton(
                        onClick = { viewModel.setShowImportDialog(true) },
                        modifier = Modifier.testTag("action_import_xml")
                    ) {
                        Icon(Icons.Default.FileOpen, contentDescription = "Import XML")
                    }

                    // Reset to defaults
                    IconButton(
                        onClick = { viewModel.setShowResetConfirmDialog(true) },
                        modifier = Modifier.testTag("action_reset_default")
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset to Defaults")
                    }

                    // Save / Commit action
                    FilledTonalButton(
                        onClick = { viewModel.saveChanges() },
                        enabled = uiState.hasUnsavedChanges,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("action_save_manifest")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Manifest")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (uiState.hasUnsavedChanges) "Save*" else "Saved")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.testTag("bottom_navigation")) {
                NavigationBarItem(
                    selected = uiState.currentTab == ScreenTab.Form,
                    onClick = { viewModel.setTab(ScreenTab.Form) },
                    icon = { Icon(Icons.Default.EditNote, contentDescription = "Form") },
                    label = { Text("Form") },
                    modifier = Modifier.testTag("nav_item_form")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == ScreenTab.Inspector,
                    onClick = { viewModel.setTab(ScreenTab.Inspector) },
                    icon = { Icon(Icons.Default.Android, contentDescription = "APK Inspector") },
                    label = { Text("APK Info") },
                    modifier = Modifier.testTag("nav_item_inspector")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == ScreenTab.XmlPreview,
                    onClick = { viewModel.setTab(ScreenTab.XmlPreview) },
                    icon = { Icon(Icons.Default.Code, contentDescription = "XML Preview") },
                    label = { Text("XML") },
                    modifier = Modifier.testTag("nav_item_xml")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == ScreenTab.Diff,
                    onClick = { viewModel.setTab(ScreenTab.Diff) },
                    icon = { Icon(Icons.Default.Difference, contentDescription = "Diff Viewer") },
                    label = { Text("Diff") },
                    modifier = Modifier.testTag("nav_item_diff")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == ScreenTab.Presets,
                    onClick = { viewModel.setTab(ScreenTab.Presets) },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = "Presets") },
                    label = { Text("Presets") },
                    modifier = Modifier.testTag("nav_item_presets")
                )
            }
        },
        floatingActionButton = {
            if (uiState.currentTab == ScreenTab.Form && uiState.hasUnsavedChanges) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.saveChanges() },
                    icon = { Icon(Icons.Default.Check, contentDescription = "Save") },
                    text = { Text("Save Changes") },
                    modifier = Modifier.testTag("fab_save_changes")
                )
            } else if (uiState.currentTab == ScreenTab.Inspector) {
                ExtendedFloatingActionButton(
                    onClick = { apkFilePickerLauncher.launch("*/*") },
                    icon = { Icon(Icons.Default.FolderZip, contentDescription = "Select APK") },
                    text = { Text("Select APK") },
                    modifier = Modifier.testTag("fab_select_apk")
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                ScreenTab.Form -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(formScrollState)
                    ) {
                        ValidationBanner(
                            validation = uiState.validation,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        ManifestFormScreen(
                            manifest = uiState.manifest,
                            viewModel = viewModel
                        )
                    }
                }
                ScreenTab.Inspector -> {
                    ApkInspectorDisplay(
                        apkDetails = uiState.loadedApkDetails,
                        isLoading = uiState.isAnalyzingApk,
                        progress = uiState.processingProgress,
                        onPickApkClick = { apkFilePickerLauncher.launch("*/*") },
                        onLoadIntoEditorClick = { viewModel.transferApkDetailsToEditor() },
                        viewModel = viewModel
                    )
                }
                ScreenTab.XmlPreview -> {
                    XmlPreviewScreen(
                        xmlContent = uiState.generatedXmlPreview,
                        viewModel = viewModel
                    )
                }
                ScreenTab.Diff -> {
                    DiffViewerScreen(
                        current = uiState.manifest,
                        original = uiState.originalManifest
                    )
                }
                ScreenTab.Presets -> {
                    PresetsScreen(
                        presets = uiState.presets,
                        history = uiState.history,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Modal dialog with circular progress indicator if an APK is processing in the background
    if (uiState.isAnalyzingApk) {
        ApkProcessingDialog(
            progress = uiState.processingProgress
        )
    }

    // Dialogs
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
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
