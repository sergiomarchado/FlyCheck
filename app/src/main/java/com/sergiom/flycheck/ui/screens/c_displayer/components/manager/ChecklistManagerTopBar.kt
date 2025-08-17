package com.sergiom.flycheck.ui.screens.c_displayer.components.manager

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

/** TopBar con flecha de navegación atrás. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChecklistManagerTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Tus checklists") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }
    )
}
