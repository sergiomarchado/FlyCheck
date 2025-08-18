package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.sergiom.flycheck.R
import com.sergiom.flycheck.ui.common.LOGO_LETTERS_COLOR
import com.sergiom.flycheck.ui.theme.LocalIsDarkTheme
import com.sergiom.flycheck.ui.theme.ThemeMode
/**
 * Barra superior (TopAppBar) de la pantalla del displayer.
 *
 * Muestra:
 * - Título de la pantalla.
 * - Botón de **back**.
 * - Botón para abrir el selector de **secciones** (habilitado/deshabilitado).
 * - Botón para **alternar el tema** (system/light/dark).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DisplayerTopBar(
    title: String,
    onBack: () -> Unit,
    onOpenSections: () -> Unit,
    sectionsEnabled: Boolean,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit
) {
    // Determina esquema de color del contenedor según modo oscuro/claro actual
    val isDark = LocalIsDarkTheme.current
    val container = if (isDark) MaterialTheme.colorScheme.tertiary
    else MaterialTheme.colorScheme.primary
    val iconColor = MaterialTheme.colorScheme.onPrimary

    // Selección del icono de tema según el modo activo
    val themeIconRes = when (themeMode) {
        ThemeMode.SYSTEM -> R.drawable.ic_theme_auto
        ThemeMode.LIGHT  -> R.drawable.ic_theme_light
        ThemeMode.DARK   -> R.drawable.ic_theme_dark
    }

    TopAppBar(
        title = { Text(title, color = LOGO_LETTERS_COLOR) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Volver",
                    tint = iconColor
                )
            }
        },
        actions = {
            // Abrir selector de secciones (deshabilitado si no hay secciones)
            IconButton(onClick = onOpenSections, enabled = sectionsEnabled) {
                Icon(
                    painter = painterResource(R.drawable.ic_list),
                    contentDescription = "Secciones",
                    tint = iconColor
                )
            }
            // Alternar tema (system/light/dark)
            IconButton(onClick = onToggleTheme) {
                Icon(
                    painter = painterResource(themeIconRes),
                    contentDescription = "Cambiar tema",
                    tint = iconColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = container,
            titleContentColor = LOGO_LETTERS_COLOR,
            navigationIconContentColor = iconColor,
            actionIconContentColor = iconColor
        )
    )
}
