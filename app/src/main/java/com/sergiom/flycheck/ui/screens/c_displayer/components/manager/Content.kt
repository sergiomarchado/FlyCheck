package com.sergiom.flycheck.ui.screens.c_displayer.components.manager

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import com.sergiom.flycheck.data.models.ChecklistInfo

/** Decide qué mostrar según loading/error/empty/lista. */
@Composable
internal fun ChecklistManagerContent(
    modifier: Modifier = Modifier,
    items: List<ChecklistInfo>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onSelect: (ChecklistInfo) -> Unit,
    onRequestRename: (ChecklistInfo) -> Unit,
    onDelete: (ChecklistInfo) -> Unit
) {
    Box(modifier) {
        when {
            isLoading -> LoadingContent()
            error != null -> ErrorCard(error = error, onRetry = onRetry)
            items.isEmpty() -> EmptyStateCard()
            else -> ManagerList(items, onSelect, onRequestRename, onDelete)
        }
    }
}
