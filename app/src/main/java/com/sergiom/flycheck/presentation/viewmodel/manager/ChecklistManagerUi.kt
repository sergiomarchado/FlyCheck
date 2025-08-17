package com.sergiom.flycheck.presentation.viewmodel.manager

import com.sergiom.flycheck.data.local.ChecklistInfo

/** Estado de UI del gestor. */
data class ManagerUiState(
    val isLoading: Boolean = false,
    val items: List<ChecklistInfo> = emptyList(),
    val error: String? = null
)

/** Efectos one–shot (snackbars, etc.). */
sealed interface ManagerEffect {
    data class ShowMessage(val text: String) : ManagerEffect
}
