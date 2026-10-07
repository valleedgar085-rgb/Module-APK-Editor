package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WorkbenchColorScheme = darkColorScheme(
    primary = WorkbenchCyan,
    onPrimary = WorkbenchBackground,
    primaryContainer = WorkbenchActive,
    onPrimaryContainer = WorkbenchCyan,
    secondary = WorkbenchLime,
    onSecondary = WorkbenchBackground,
    secondaryContainer = WorkbenchElevated,
    onSecondaryContainer = WorkbenchLime,
    tertiary = WorkbenchWarning,
    onTertiary = WorkbenchBackground,
    tertiaryContainer = WorkbenchActive,
    onTertiaryContainer = WorkbenchWarning,
    background = WorkbenchBackground,
    onBackground = WorkbenchTextPrimary,
    surface = WorkbenchSurface,
    onSurface = WorkbenchTextPrimary,
    surfaceVariant = WorkbenchElevated,
    onSurfaceVariant = WorkbenchTextSecondary,
    outline = WorkbenchBorder,
    outlineVariant = WorkbenchBorder,
    error = WorkbenchError,
    onError = WorkbenchBackground,
    errorContainer = WorkbenchActive,
    onErrorContainer = WorkbenchError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = WorkbenchColorScheme,
        typography = Typography,
        content = content
    )
}
