package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ApkProcessingProgress
import com.example.model.LoadedApkDetails
import com.example.model.ManifestActivity
import com.example.model.ManifestData
import com.example.model.ManifestProfile
import com.example.model.ManifestValidator
import com.example.model.ValidationResult
import com.example.repository.ManifestRepository
import com.example.util.ApkParserHelper
import com.example.util.ManifestXmlParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ScreenTab(val title: String) {
    object Form : ScreenTab("Manifest Form")
    object Inspector : ScreenTab("APK Inspector")
    object XmlPreview : ScreenTab("XML Preview")
    object Presets : ScreenTab("Presets")
    object Diff : ScreenTab("Diff Viewer")
}

data class ManifestUiState(
    val manifest: ManifestData = ManifestData(),
    val originalManifest: ManifestData = ManifestData(),
    val currentTab: ScreenTab = ScreenTab.Form,
    val validation: ValidationResult = ValidationResult(true),
    val presets: List<ManifestProfile> = emptyList(),
    val history: List<ManifestProfile> = emptyList(),
    val snackbarMessage: String? = null,
    val showSavePresetDialog: Boolean = false,
    val showAddActivityDialog: Boolean = false,
    val showAddPermissionDialog: Boolean = false,
    val showImportDialog: Boolean = false,
    val showResetConfirmDialog: Boolean = false,
    val generatedXmlPreview: String = "",
    val hasUnsavedChanges: Boolean = false,
    val loadedApkDetails: LoadedApkDetails? = null,
    val isAnalyzingApk: Boolean = false,
    val processingProgress: ApkProcessingProgress = ApkProcessingProgress()
)

class ManifestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ManifestRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(ManifestUiState())
    val uiState: StateFlow<ManifestUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        val loaded = repository.loadCurrentManifest()
        val presets = repository.getPresets()
        val history = repository.getHistory()
        val xml = ManifestXmlParser.serialize(loaded)
        val validation = ManifestValidator.validate(loaded)

        _uiState.update {
            it.copy(
                manifest = loaded,
                originalManifest = loaded,
                presets = presets,
                history = history,
                validation = validation,
                generatedXmlPreview = xml,
                hasUnsavedChanges = false
            )
        }
    }

    fun setTab(tab: ScreenTab) {
        if (tab == ScreenTab.XmlPreview) {
            refreshXmlPreview()
        }
        _uiState.update { it.copy(currentTab = tab) }
    }

    // APK File Loading and Parsing
    fun onApkFileSelected(uri: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAnalyzingApk = true,
                    processingProgress = ApkProcessingProgress(
                        stage = "Opening APK stream...",
                        currentStep = 1,
                        totalSteps = 4,
                        percentage = 0.15f
                    )
                )
            }
            val result = withContext(Dispatchers.IO) {
                ApkParserHelper.parseApkFromUri(getApplication(), uri) { progress ->
                    _uiState.update { it.copy(processingProgress = progress) }
                }
            }

            result.fold(
                onSuccess = { (apkDetails, manifestData) ->
                    val xml = ManifestXmlParser.serialize(manifestData)
                    val validation = ManifestValidator.validate(manifestData)
                    _uiState.update {
                        it.copy(
                            loadedApkDetails = apkDetails,
                            manifest = manifestData,
                            originalManifest = manifestData,
                            generatedXmlPreview = xml,
                            validation = validation,
                            isAnalyzingApk = false,
                            hasUnsavedChanges = false,
                            currentTab = ScreenTab.Inspector,
                            snackbarMessage = "Parsed ${apkDetails.fileName}: Package ${apkDetails.packageName}, Version ${apkDetails.versionName} (${apkDetails.versionCode})"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isAnalyzingApk = false,
                            snackbarMessage = "Failed to parse APK: ${error.localizedMessage ?: "Unknown error"}"
                        )
                    }
                }
            )
        }
    }

    fun loadSampleApkDemo() {
        val demoBitmap = android.graphics.Bitmap.createBitmap(128, 128, android.graphics.Bitmap.Config.ARGB_8888).apply {
            val canvas = android.graphics.Canvas(this)
            val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#4F46E5")
            }
            canvas.drawRoundRect(0f, 0f, 128f, 128f, 28f, 28f, paint)
            val iconPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                style = android.graphics.Paint.Style.FILL
            }
            canvas.drawCircle(64f, 64f, 32f, iconPaint)
            iconPaint.color = android.graphics.Color.parseColor("#4F46E5")
            canvas.drawCircle(64f, 64f, 20f, iconPaint)
        }

        val demoDetails = LoadedApkDetails(
            fileName = "sample_app-release.apk",
            fileSizeFormatted = "18.4 MB",
            fileSizeBytes = 19293798L,
            packageName = "com.sample.photostudio",
            versionCode = 142,
            versionName = "3.2.1-pro",
            appLabel = "PhotoStudio FX",
            minSdk = 24,
            targetSdk = 35,
            compileSdk = 35,
            isDebuggable = false,
            permissions = listOf(
                "android.permission.INTERNET",
                "android.permission.ACCESS_NETWORK_STATE",
                "android.permission.CAMERA",
                "android.permission.POST_NOTIFICATIONS",
                "android.permission.VIBRATE"
            ),
            activities = listOf(
                "com.sample.photostudio.MainActivity",
                "com.sample.photostudio.EditorActivity",
                "com.sample.photostudio.FilterPreviewActivity"
            ),
            services = listOf(
                "com.sample.photostudio.ExportRenderService"
            ),
            totalDexCount = 2,
            hasRawXml = true,
            iconBitmap = demoBitmap
        )

        val demoManifest = ManifestData(
            packageName = demoDetails.packageName,
            versionCode = demoDetails.versionCode,
            versionName = demoDetails.versionName,
            appLabel = demoDetails.appLabel,
            minSdk = demoDetails.minSdk,
            targetSdk = demoDetails.targetSdk,
            compileSdk = demoDetails.compileSdk,
            isDebuggable = demoDetails.isDebuggable,
            permissions = demoDetails.permissions,
            activities = demoDetails.activities.mapIndexed { idx, name ->
                ManifestActivity(
                    name = name,
                    label = if (idx == 0) demoDetails.appLabel else name.substringAfterLast("."),
                    exported = idx == 0,
                    isLauncher = idx == 0
                )
            }
        )

        val xml = ManifestXmlParser.serialize(demoManifest)
        val validation = ManifestValidator.validate(demoManifest)

        _uiState.update {
            it.copy(
                loadedApkDetails = demoDetails,
                manifest = demoManifest,
                originalManifest = demoManifest,
                generatedXmlPreview = xml,
                validation = validation,
                hasUnsavedChanges = false,
                currentTab = ScreenTab.Inspector,
                snackbarMessage = "Loaded sample APK: PhotoStudio FX (v3.2.1-pro)"
            )
        }
    }

    fun transferApkDetailsToEditor() {
        _uiState.update {
            it.copy(
                currentTab = ScreenTab.Form,
                snackbarMessage = "Loaded APK manifest details into Form Editor."
            )
        }
    }

    fun updatePackageName(pkg: String) {
        updateField { it.copy(packageName = pkg.trim()) }
    }

    fun updateVersionCode(code: Int) {
        updateField { it.copy(versionCode = code) }
    }

    fun incrementVersionCode() {
        updateField { it.copy(versionCode = it.versionCode + 1) }
    }

    fun decrementVersionCode() {
        if (_uiState.value.manifest.versionCode > 1) {
            updateField { it.copy(versionCode = it.versionCode - 1) }
        }
    }

    fun updateVersionName(name: String) {
        updateField { it.copy(versionName = name.trim()) }
    }

    fun updateAppLabel(label: String) {
        updateField { it.copy(appLabel = label) }
    }

    fun updateMinSdk(sdk: Int) {
        updateField { it.copy(minSdk = sdk) }
    }

    fun updateTargetSdk(sdk: Int) {
        updateField { it.copy(targetSdk = sdk) }
    }

    fun updateCompileSdk(sdk: Int) {
        updateField { it.copy(compileSdk = sdk) }
    }

    fun updateIcon(icon: String) {
        updateField { it.copy(iconDrawable = icon.trim()) }
    }

    fun updateRoundIcon(icon: String) {
        updateField { it.copy(roundIconDrawable = icon.trim()) }
    }

    fun updateTheme(theme: String) {
        updateField { it.copy(theme = theme.trim()) }
    }

    fun toggleDebuggable(enabled: Boolean) {
        updateField { it.copy(isDebuggable = enabled) }
    }

    fun toggleAllowBackup(enabled: Boolean) {
        updateField { it.copy(allowBackup = enabled) }
    }

    fun toggleSupportsRtl(enabled: Boolean) {
        updateField { it.copy(supportsRtl = enabled) }
    }

    fun toggleUsesCleartextTraffic(enabled: Boolean) {
        updateField { it.copy(usesCleartextTraffic = enabled) }
    }

    fun toggleRequestLegacyExternalStorage(enabled: Boolean) {
        updateField { it.copy(requestLegacyExternalStorage = enabled) }
    }

    fun addPermission(permission: String) {
        val perm = permission.trim()
        if (perm.isBlank()) return
        val currentList = _uiState.value.manifest.permissions.toMutableList()
        if (!currentList.contains(perm)) {
            currentList.add(perm)
            updateField { it.copy(permissions = currentList) }
        }
    }

    fun removePermission(permission: String) {
        val currentList = _uiState.value.manifest.permissions.toMutableList()
        currentList.remove(permission)
        updateField { it.copy(permissions = currentList) }
    }

    fun addActivity(activity: ManifestActivity) {
        val currentList = _uiState.value.manifest.activities.toMutableList()
        currentList.add(activity)
        updateField { it.copy(activities = currentList) }
    }

    fun removeActivity(index: Int) {
        val currentList = _uiState.value.manifest.activities.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            updateField { it.copy(activities = currentList) }
        }
    }

    fun updateActivity(index: Int, updated: ManifestActivity) {
        val currentList = _uiState.value.manifest.activities.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = updated
            updateField { it.copy(activities = currentList) }
        }
    }

    private inline fun updateField(updateFunc: (ManifestData) -> ManifestData) {
        _uiState.update { state ->
            val updatedManifest = updateFunc(state.manifest)
            val validation = ManifestValidator.validate(updatedManifest)
            val hasChanges = updatedManifest != state.originalManifest
            state.copy(
                manifest = updatedManifest,
                validation = validation,
                hasUnsavedChanges = hasChanges,
                generatedXmlPreview = ManifestXmlParser.serialize(updatedManifest)
            )
        }
    }

    fun refreshXmlPreview() {
        val xml = ManifestXmlParser.serialize(_uiState.value.manifest)
        _uiState.update { it.copy(generatedXmlPreview = xml) }
    }

    fun saveChanges() {
        viewModelScope.launch {
            val manifest = _uiState.value.manifest
            val xml = repository.saveManifest(manifest)
            val history = repository.getHistory()
            _uiState.update {
                it.copy(
                    originalManifest = manifest,
                    history = history,
                    hasUnsavedChanges = false,
                    generatedXmlPreview = xml,
                    snackbarMessage = "AndroidManifest.xml successfully saved!"
                )
            }
        }
    }

    fun resetToDefaults() {
        val defaultData = repository.resetToDefault()
        val xml = ManifestXmlParser.serialize(defaultData)
        val validation = ManifestValidator.validate(defaultData)
        _uiState.update {
            it.copy(
                manifest = defaultData,
                originalManifest = defaultData,
                validation = validation,
                generatedXmlPreview = xml,
                hasUnsavedChanges = false,
                snackbarMessage = "Reset to original default manifest values"
            )
        }
    }

    fun discardChanges() {
        val original = _uiState.value.originalManifest
        val xml = ManifestXmlParser.serialize(original)
        val validation = ManifestValidator.validate(original)
        _uiState.update {
            it.copy(
                manifest = original,
                validation = validation,
                generatedXmlPreview = xml,
                hasUnsavedChanges = false,
                snackbarMessage = "Changes discarded"
            )
        }
    }

    fun applyPreset(preset: ManifestProfile) {
        updateField { preset.manifest }
        _uiState.update {
            it.copy(
                snackbarMessage = "Loaded preset: ${preset.name}",
                currentTab = ScreenTab.Form
            )
        }
    }

    fun saveAsPreset(name: String, description: String) {
        val manifest = _uiState.value.manifest
        repository.saveCustomPreset(name, description, manifest)
        val presets = repository.getPresets()
        _uiState.update {
            it.copy(
                presets = presets,
                snackbarMessage = "Preset '$name' created!",
                showSavePresetDialog = false
            )
        }
    }

    fun importXml(xmlContent: String) {
        try {
            val parsed = repository.importXml(xmlContent)
            val xml = ManifestXmlParser.serialize(parsed)
            val validation = ManifestValidator.validate(parsed)
            val history = repository.getHistory()
            _uiState.update {
                it.copy(
                    manifest = parsed,
                    originalManifest = parsed,
                    history = history,
                    validation = validation,
                    generatedXmlPreview = xml,
                    hasUnsavedChanges = false,
                    snackbarMessage = "Manifest XML imported and applied successfully!",
                    showImportDialog = false
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(snackbarMessage = "Failed to parse XML: ${e.localizedMessage}")
            }
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun setShowSavePresetDialog(show: Boolean) {
        _uiState.update { it.copy(showSavePresetDialog = show) }
    }

    fun setShowAddActivityDialog(show: Boolean) {
        _uiState.update { it.copy(showAddActivityDialog = show) }
    }

    fun setShowAddPermissionDialog(show: Boolean) {
        _uiState.update { it.copy(showAddPermissionDialog = show) }
    }

    fun setShowImportDialog(show: Boolean) {
        _uiState.update { it.copy(showImportDialog = show) }
    }

    fun setShowResetConfirmDialog(show: Boolean) {
        _uiState.update { it.copy(showResetConfirmDialog = show) }
    }
}
