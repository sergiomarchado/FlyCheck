package com.sergiom.flycheck.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.model.CheckListItem

@Composable
fun CheckListSectionEditor(
    sectionId: String,
    title: String,
    items: List<CheckListItem>,
    onTitleChange: (String) -> Unit,
    onAddItem: (String, String) -> Unit,
    onToggleItemChecked: (String) -> Unit,
    onItemTitleChange: (String, String) -> Unit,
    onItemActionChange: (String, String) -> Unit
) {
    var showEditDialog by rememberSaveable(sectionId + "_dialog") { mutableStateOf(false) }
    var editedTitle by rememberSaveable(sectionId + "_title") { mutableStateOf(title) }

    var showAddFields by remember { mutableStateOf(false) }
    var newItemTitle by rememberSaveable(sectionId + "_newitem_title") { mutableStateOf("") }
    var newItemAction by rememberSaveable(sectionId + "_newitem_action") { mutableStateOf("") }

    // Cabecera editable
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { showEditDialog = true }) {
            Icon(Icons.Default.Edit, contentDescription = "Editar sección")
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar título de sección") },
            text = {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    singleLine = true,
                    label = { Text("Nuevo título") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onTitleChange(editedTitle)
                    showEditDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            CheckListItemCard(
                item = item,
                onToggleChecked = { onToggleItemChecked(item.id) },
                onTitleChange = { onItemTitleChange(item.id, it) },
                onActionChange = { onItemActionChange(item.id, it) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (showAddFields) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newItemTitle,
                        onValueChange = { newItemTitle = it },
                        label = { Text("Item") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = newItemAction,
                        onValueChange = { newItemAction = it },
                        label = { Text("Acción") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = {
                        if (newItemTitle.isNotBlank() || newItemAction.isNotBlank()) {
                            onAddItem(newItemTitle, newItemAction)
                            newItemTitle = ""
                            newItemAction = ""
                            showAddFields = false
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Aceptar")
                }
            }
        } else {
            Button(
                onClick = { showAddFields = true },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("Añadir ítem")
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}


