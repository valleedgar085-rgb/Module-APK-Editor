package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val WorkbenchDisplay = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 28.sp,
    lineHeight = 34.sp,
    letterSpacing = (-0.4).sp
)

val WorkbenchTitle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    lineHeight = 24.sp
)

val WorkbenchSubtitle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp
)

val WorkbenchBody = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
)

val WorkbenchLabel = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.8.sp
)

val WorkbenchCode = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 18.sp
)

val Typography = Typography(
    displaySmall = WorkbenchDisplay,
    titleLarge = WorkbenchTitle,
    titleMedium = WorkbenchTitle,
    titleSmall = WorkbenchSubtitle,
    bodyLarge = WorkbenchBody,
    bodyMedium = WorkbenchBody,
    bodySmall = WorkbenchBody.copy(fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = WorkbenchLabel,
    labelMedium = WorkbenchLabel,
    labelSmall = WorkbenchLabel.copy(fontSize = 10.sp, lineHeight = 14.sp)
)
