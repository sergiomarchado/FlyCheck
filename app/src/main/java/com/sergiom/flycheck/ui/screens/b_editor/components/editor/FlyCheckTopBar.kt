package com.sergiom.flycheck.ui.screens.b_editor.components.editor


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sergiom.flycheck.R
import com.sergiom.flycheck.ui.common.LOGO_LETTERS_COLOR
import com.sergiom.flycheck.ui.theme.LocalIsDarkTheme

/**
 * Componente visual de barra superior (TopAppBar) reutilizable.
 * Incluye botón de retroceso, título, y menú desplegable de opciones (como exportar o ajustes).
 *
 * @param onBackClick Callback que se ejecuta al pulsar el botón de retroceso.
 * @param onMenuOptionClick Callback que se ejecuta al seleccionar una opción del menú.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlyCheckTopBar(
    onBackClick: () -> Unit = {},
    onMenuOptionClick: (String) -> Unit = {}
) {

    // Detecta si el sistema está en modo oscuro para ajustar el color del TopAppBar.
    val isDark = LocalIsDarkTheme.current
    val defaultTopBarColor =
        if(isDark) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

    TopAppBar(
        title = {
            // Título con nombre de la app
            Text(
                text = stringResource(R.string.app_name) + " ✈️",
                color = LOGO_LETTERS_COLOR) },
        // Icono de retroceso (mirrored para soportar RTL)
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.topbar_back_contentdescription),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            // Estado local para mostrar/ocultar el menú desplegable
            var expanded by remember { mutableStateOf(false) }

            // Botón del icono de menú
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.topbar_back_icon_menu),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Menú desplegable con opciones
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.topbar_dropdown_menu_item_export_pdf)) },
                    onClick = {
                        expanded = false
                        onMenuOptionClick("export_pdf") // Identificador que el controlador puede manejar
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.topbar_dropdown_menu_item_settings)) },
                    onClick = {
                        expanded = false
                        onMenuOptionClick("settings")
                    }
                )
            }
        },
        // Colores personalizados para el TopAppBar
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = defaultTopBarColor,
            titleContentColor = Color.White
        )
    )
}


