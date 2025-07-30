package com.sergiom.flycheck.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListItem

@Composable
fun CheckListSectionEditor(
    sectionId: String,
    title: String,
    items: List<CheckListItem>,
    onTitleChange: (String) -> Unit,
    onAddItem: (String) -> Unit,
    onToggleItemChecked: (String) -> Unit,
    onItemTitleChange: (String, String) -> Unit,
    onItemActionChange: (String, String) -> Unit
) {
    var showEditDialog by rememberSaveable(sectionId + "_dialog") { mutableStateOf(false) }
    var editedTitle by rememberSaveable(sectionId + "_title") { mutableStateOf(title) }
    var newItemText by rememberSaveable(sectionId + "_newitem") { mutableStateOf("") }

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
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Añadir nuevo ítem
    Row {
        OutlinedTextField(
            value = newItemText,
            onValueChange = { newItemText = it },
            label = { Text(stringResource(R.string.checklisteditorscreen_item_text)) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {
                if (newItemText.isNotBlank()) {
                    onAddItem(newItemText)
                    newItemText = ""
                }
            }
        ) {
            Text(
                text = stringResource(R.string.checklisteditorscreen_add),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}


