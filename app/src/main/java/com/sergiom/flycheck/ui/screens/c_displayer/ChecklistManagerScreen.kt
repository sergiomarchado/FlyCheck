package com.sergiom.flycheck.ui.screens.c_displayer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.models.ChecklistInfo
import com.sergiom.flycheck.ui.screens.c_displayer.components.manager.ChecklistManagerContent
import com.sergiom.flycheck.ui.screens.c_displayer.components.manager.ChecklistManagerTopBar
import com.sergiom.flycheck.ui.screens.c_displayer.components.manager.RenameDialog

/**
 * Lista de checklists con TopBar, estados (loading/error/empty) y diálogo de renombrado.
 * Integrada con el sistema de tema (usa los mismos colores que el resto).
 */
@Composable
fun ChecklistManagerScreen(
    items: List<ChecklistInfo>,
    onSelect: (ChecklistInfo) -> Unit,
    onDelete: (ChecklistInfo) -> Unit,
    onRename: (ChecklistInfo, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: String? = null,
    onRetry: () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    // Opcional: botón para alternar tema en el TopBar
    onToggleTheme: (() -> Unit)? = null
) {
    var renameTarget by remember { mutableStateOf<ChecklistInfo?>(null) }
    var newName by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = { ChecklistManagerTopBar(onBack = onBack, onToggleTheme = onToggleTheme) },
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ChecklistManagerContent(
                items = items,
                isLoading = isLoading,
                error = error,
                onRetry = onRetry,
                onSelect = onSelect,
                onRequestRename = { info ->
                    renameTarget = info
                    newName = info.name
                },
                onDelete = onDelete
            )
        }
    }

    if (renameTarget != null) {
        RenameDialog(
            value = newName,
            onValueChange = { newName = it },
            onDismiss = { renameTarget = null },
            onConfirm = {
                renameTarget?.let { onRename(it, newName.trim()) }
                renameTarget = null
            }
        )
    }
}
