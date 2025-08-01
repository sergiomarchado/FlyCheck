package com.sergiom.flycheck.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
    surface = BlueDarkSurface,               // Cards, diálogos, etc.
    surfaceVariant = BlueDarkPrimary,
    onSurface = TextOnDarkPrimary,           // Texto sobre cards
    primaryContainer = DarkModeAccent,
    secondaryContainer = SubSectionDark,
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
    surface = SoftSurface,
    surfaceVariant = BlueDarkPrimary,// Surface claro neutro
    onSurface = GreyTextLight
)

@Composable
fun FlyCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Usamos nuestra paleta personalizada
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !darkTheme -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
