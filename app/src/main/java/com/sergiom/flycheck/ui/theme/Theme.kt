package com.sergiom.flycheck.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Cyan30,               // Color de acento azul elegante
    onPrimary = Color.White,                 // Texto sobre botones/acento
    secondary = Cyan30,                      // Acento secundario llamativo
    onSecondary = Color.White,               // Texto sobre secundario
    tertiary = DeepBluePrimary,                       // Alternativa más clara
    background = BlueDarkBackground,         // Fondo principal (pantallas)
    onBackground = BlueDarkText,             // Texto sobre fondo
    surface = BlueDarkSubSection,               // Cards, diálogos, etc.
    surfaceVariant = BlueDarkPrimary,
    onSurface = TextOnDarkPrimary,           // Texto sobre cards
    surfaceTint = BlueDarkSurface,
    primaryContainer = DarkModeAccent,
    secondaryContainer = SubSectionDarkP2,
    onSecondaryContainer = TextOnDarkSecondary, // Texto atenuado
    outline = BlueDarkBorder                 // Bordes, líneas de campos
)

private val LightColorScheme = lightColorScheme(
    primary = DeepBluePrimary,                  // Azul claro
    onPrimary = Color.White,
    secondary = Blue40,
    tertiary = Cyan30,
    onSecondary = Color.White,
    background = SoftWhite,
    primaryContainer = DeepBlueHeader,
    secondaryContainer = SubSectionDark,
    onSecondaryContainer = GreyLight,
    onBackground = GreyTextDark,
    surface = SubSectionDark,
    surfaceTint = SoftSurface,
    surfaceVariant = BlueDarkPrimary,// Surface claro neutro
    onSurface = GreyTextLight
)

@Composable
fun FlyCheckTheme(
    mode: ThemeMode,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val dark = when (mode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark  -> DarkColorScheme
        else  -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalAppThemeMode provides mode,
        LocalIsDarkTheme provides dark
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}


/* ===== OPCIONAL: overload de compatibilidad ===== */
@Composable
fun FlyCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FlyCheckTheme(
        mode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
        dynamicColor = dynamicColor,
        content = content
    )
}
