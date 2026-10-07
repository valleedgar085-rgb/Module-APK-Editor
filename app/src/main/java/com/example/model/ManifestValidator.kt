package com.example.model

/**
 * Validation result for AndroidManifest fields
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
) {
    val hasIssues: Boolean get() = errors.isNotEmpty() || warnings.isNotEmpty()
}

object ManifestValidator {

    private val PACKAGE_NAME_REGEX = Regex("^[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)+$")

    fun validate(data: ManifestData): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        // 1. Package name checks
        if (data.packageName.isBlank()) {
            errors.add("Package name cannot be empty.")
        } else if (!PACKAGE_NAME_REGEX.matches(data.packageName)) {
            errors.add("Invalid package name format (must follow reverse-domain style e.g., com.example.app).")
        }

        // 2. App label
        if (data.appLabel.isBlank()) {
            errors.add("App label (title) cannot be empty.")
        } else if (data.appLabel.length > 50) {
            warnings.add("App label is very long (${data.appLabel.length} chars). Consider shortening for better launcher display.")
        }

        // 3. Version Code
        if (data.versionCode <= 0) {
            errors.add("Version code must be a positive integer greater than 0.")
        } else if (data.versionCode > 2100000000) {
            errors.add("Version code exceeds maximum allowable Google Play integer limit.")
        }

        // 4. Version Name
        if (data.versionName.isBlank()) {
            errors.add("Version name cannot be empty (e.g., 1.0.0).")
        }

        // 5. SDK levels
        if (data.minSdk < 1) {
            errors.add("minSdkVersion must be at least 1.")
        } else if (data.minSdk < 21) {
            warnings.add("minSdkVersion is below 21 (Android 5.0). Most modern libraries require minSdk 21 or 24.")
        }

        if (data.targetSdk < data.minSdk) {
            errors.add("targetSdkVersion ($data.targetSdk) cannot be lower than minSdkVersion ($data.minSdk).")
        } else if (data.targetSdk < 34) {
            warnings.add("targetSdkVersion is lower than 34. Google Play policy requires recent target SDK levels.")
        }

        // 6. Security flags warnings
        if (data.isDebuggable) {
            warnings.add("debuggable=\"true\" should NOT be enabled for release builds as it poses security risks.")
        }
        if (data.usesCleartextTraffic) {
            warnings.add("usesCleartextTraffic=\"true\" allows unencrypted HTTP network communication.")
        }
        if (data.requestLegacyExternalStorage) {
            warnings.add("requestLegacyExternalStorage is deprecated in Android 11+ (API 30).")
        }

        // 7. Activities checks
        if (data.activities.isEmpty()) {
            warnings.add("No activities declared in manifest. The app may have no launchable UI.")
        } else {
            val launchers = data.activities.filter { it.isLauncher }
            if (launchers.isEmpty()) {
                warnings.add("No launcher activity marked (MAIN / LAUNCHER). The app won't appear on home screen launcher.")
            } else if (launchers.size > 1) {
                warnings.add("Multiple launcher activities found. This will create multiple app drawer icons.")
            }

            for (act in data.activities) {
                if (act.name.isBlank()) {
                    errors.add("Activity name cannot be blank.")
                }
                if (act.isLauncher && !act.exported) {
                    errors.add("Launcher activity '${act.name}' must have exported=\"true\" on Android 12+.")
                }
            }
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
}
