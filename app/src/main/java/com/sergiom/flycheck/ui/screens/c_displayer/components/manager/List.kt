package com.sergiom.flycheck.ui.screens.c_displayer.components.manager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.models.ChecklistInfo

/** Lista de checklists. */
@Composable
internal fun ManagerList(
    items: List<ChecklistInfo>,
    onSelect: (ChecklistInfo) -> Unit,
    onRequestRename: (ChecklistInfo) -> Unit,
    onDelete: (ChecklistInfo) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ManagerItemCard(
                info = item,
                onClick = { onSelect(item) },
                onRenameClick = { onRequestRename(item) },
                onDeleteClick = { onDelete(item) }
            )
        }
    }
}

/** Tarjeta de cada checklist. */
@Composable
internal fun ManagerItemCard(
    info: ChecklistInfo,
    onClick: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(info.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(2.dp))
            Text("${info.model} • ${info.airline}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onRenameClick) {
                    Icon(Icons.Filled.Edit, contentDescription = "Renombrar")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
