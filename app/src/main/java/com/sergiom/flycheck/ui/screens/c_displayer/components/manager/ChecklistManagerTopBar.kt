package com.sergiom.flycheck.ui.screens.c_displayer.components.manager

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.sergiom.flycheck.R
import com.sergiom.flycheck.ui.common.LOGO_LETTERS_COLOR
import com.sergiom.flycheck.ui.theme.LocalAppThemeMode
import com.sergiom.flycheck.ui.theme.LocalIsDarkTheme
import com.sergiom.flycheck.ui.theme.ThemeMode

/** TopBar con la misma estética que Displayer/Editor (colores unificados + tema opcional). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChecklistManagerTopBar(
    onBack: () -> Unit,
    onToggleTheme: (() -> Unit)? = null   // si no lo pasas, no se muestra el botón
) {
    val isDark = LocalIsDarkTheme.current
    val mode = LocalAppThemeMode.current

    val container = if (isDark) MaterialTheme.colorScheme.tertiary
    else MaterialTheme.colorScheme.primary
    val iconColor = MaterialTheme.colorScheme.onPrimary

    // Icono dinámico del modo de tema actual (usa tus drawables)
    val themeIconRes = when (mode) {
        ThemeMode.SYSTEM -> R.drawable.ic_theme_auto
        ThemeMode.LIGHT  -> R.drawable.ic_theme_light
        ThemeMode.DARK   -> R.drawable.ic_theme_dark
    }

    TopAppBar(
        title = { Text("Mis Checklists (guardadas en local)", color = LOGO_LETTERS_COLOR) },
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
            if (onToggleTheme != null) {
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        painter = painterResource(themeIconRes),
                        contentDescription = "Cambiar tema",
                        tint = iconColor
                    )
                }
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
