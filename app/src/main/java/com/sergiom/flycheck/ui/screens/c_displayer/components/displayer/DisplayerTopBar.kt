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
    val isDark = LocalIsDarkTheme.current
    val container = if (isDark) MaterialTheme.colorScheme.tertiary
    else MaterialTheme.colorScheme.primary
    val iconColor = MaterialTheme.colorScheme.onPrimary

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
                    painter = painterResource(R.drawable.ic_arrow_back), // o sigue usando Icons.AutoMirrored…
                    contentDescription = "Volver",
                    tint = iconColor
                )
            }
        },
        actions = {
            IconButton(onClick = onOpenSections, enabled = sectionsEnabled) {
                Icon(
                    painter = painterResource(R.drawable.ic_list),   // o tu drawable
                    contentDescription = "Secciones",
                    tint = iconColor
                )
            }
            // Botón de tema (usa tu drawable)
            IconButton(onClick = onToggleTheme) {
                Icon(
                    painter = painterResource(themeIconRes),
                    contentDescription = "Cambiar tema",
                    // si tu vector es monocromo, puedes tintarlo:
                    tint = iconColor
                    // si es PNG a color, quita el tinte:
                    // tint = Color.Unspecified
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
