package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.SnackbarHostState
import com.sergiom.flycheck.ui.screens.c_displayer.ChecklistDisplayerScreen
import com.sergiom.flycheck.viewmodel.player.ChecklistDisplayerViewModel
import com.sergiom.flycheck.viewmodel.player.UiEvent

/**
 * Wrapper que:
 * 1) Obtiene el ViewModel con Hilt.
 * 2) Colecciona los StateFlow (uiState / flat / statuses).
 * 3) Escucha UiEvent para mostrar Snackbars.
 * 4) Inyecta todo en ChecklistDisplayerScreen.
 */
@Composable
fun ChecklistDisplayerRoute(
    onBack: () -> Unit,
    vm: ChecklistDisplayerViewModel = hiltViewModel()
) {
    val ui by vm.uiState.collectAsState()
    val flat by vm.flat.collectAsState()
    val statuses by vm.statuses.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Escucha de eventos efímeros del VM (errores, avisos, etc.)
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                is UiEvent.ShowMessage -> snackbarHostState.showSnackbar(ev.text)
            }
        }
    }

    ChecklistDisplayerScreen(
        state = ui,
        flat = flat,
        statuses = statuses,
        onToggleItem = vm::onToggleItem,
        onJumpToItem = vm::onJumpToItem,
        onSelectSection = vm::onJumpToSection,
        onBack = onBack,
        snackbarHostState = snackbarHostState
    )
}
