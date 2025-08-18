package com.sergiom.flycheck.viewmodel.player

/** Eventos efímeros desde el ViewModel hacia la UI (snackbars, toasts, etc.). */
sealed interface UiEvent {
    data class ShowMessage(val text: String) : UiEvent
}

