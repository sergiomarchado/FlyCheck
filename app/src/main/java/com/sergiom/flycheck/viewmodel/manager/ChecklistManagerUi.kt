package com.sergiom.flycheck.viewmodel.manager

import com.sergiom.flycheck.data.models.ChecklistInfo

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
