package com.educalab.staticdata.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val Color_White = androidx.compose.ui.graphics.Color(0xFFFFFFFF)

private val StaticdataLightScheme = lightColorScheme(
    primary = AmberStampDark,
    onPrimary = Color_White,
    primaryContainer = AmberStamp,
    onPrimaryContainer = InkNavy900,
    secondary = TealClueDark,
    onSecondary = Color_White,
    secondaryContainer = TealClue,
    tertiary = VioletMystery,
    background = ParchmentCream,
    onBackground = InkNavy900,
    surface = Color_White,
    onSurface = InkNavy900,
    surfaceVariant = ParchmentCreamDim,
    onSurfaceVariant = InkNavy700,
    error = ErrorRed,
    onError = Color_White,
)

private val StaticdataDarkScheme = darkColorScheme(
    primary = AmberStamp,
    onPrimary = InkNavy900,
    primaryContainer = AmberStampDark,
    onPrimaryContainer = ParchmentCream,
    secondary = TealClue,
    onSecondary = InkNavy900,
    background = InkNavy900,
    onBackground = ParchmentCream,
    surface = InkNavy800,
    onSurface = ParchmentCream,
    surfaceVariant = InkNavy700,
    onSurfaceVariant = ParchmentCreamDim,
    error = CoralAlert,
    onError = InkNavy900,
)

/**
 * Staticdata usa siempre el esquema "claro" tipo pergamino/oficina de casos
 * como identidad principal (más cálido y legible para la temática de
 * archivo detectivesco), y cae al esquema oscuro solo si el sistema lo pide.
 */
@Composable
fun StaticdataTheme(useDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (useDarkTheme) StaticdataDarkScheme else StaticdataLightScheme
    MaterialTheme(colorScheme = colorScheme, typography = StaticdataTypography, content = content)
}
