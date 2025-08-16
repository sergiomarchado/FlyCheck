package com.sergiom.flycheck.ui.screens.c_displayer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.local.ChecklistInfo

@Composable
fun ChecklistManagerScreen(
    items: List<ChecklistInfo>,
    onSelect: (ChecklistInfo) -> Unit,
    onDelete: (ChecklistInfo) -> Unit,
    onRename: (ChecklistInfo, String) -> Unit
) {
    var renameTarget by remember { mutableStateOf<ChecklistInfo?>(null) }
    var newName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (items.isEmpty()) {
            Text("No tienes checklists guardadas aún")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onSelect(item) },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(item.name, style = MaterialTheme.typography.titleMedium)
                            Text("${item.model} • ${item.airline}", style = MaterialTheme.typography.bodySmall)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(onClick = { renameTarget = item; newName = item.name }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Renombrar")
                                }
                                IconButton(onClick = { onDelete(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (renameTarget != null) {
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text("Renombrar checklist") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) {
                        onRename(renameTarget!!, newName)
                    }
                    renameTarget = null
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { renameTarget = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
