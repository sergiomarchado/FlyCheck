package com.sergiom.flycheck.ui.screens.b_custom.components.editor.item

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListBlock

@Composable
fun CheckListItemEditor(
    sectionId: String,
    title: String,
    blocks: List<CheckListBlock>,
    onTitleChange: (String) -> Unit,
    onAddItem: (String, String) -> Boolean,
    onAddSubsection: () -> Unit,
    onToggleItemChecked: (String) -> Unit,
    onItemTitleChange: (String, String) -> Unit,
    onItemActionChange: (String, String) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    var showEditDialog by rememberSaveable(sectionId + "_dialog") { mutableStateOf(false) }
    var editedTitle by rememberSaveable(sectionId + "_title") { mutableStateOf(title) }

    var showAddFields by remember { mutableStateOf(false) }
    var newItemTitle by rememberSaveable(sectionId + "_newitem_title") { mutableStateOf("") }
    var newItemAction by rememberSaveable(sectionId + "_newitem_action") { mutableStateOf("") }

    var showSubsectionForm by remember { mutableStateOf(false) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.checklistsectioneditor_alertdialog_title)) },
            text = {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    singleLine = true,
                    label = {
                        Text(stringResource(R.string.checklistsectioneditor_alertdialog_outlinedtf_label))
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onTitleChange(editedTitle)
                    showEditDialog = false
                }) {
                    Text(stringResource(R.string.checklistsectioneditor_alertdialog_confirmbutton))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(stringResource(R.string.checklistsectioneditor_alertdialog_dismissbutton))
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
        // Render de los ítems
        blocks.filterIsInstance<CheckListBlock.ItemBlock>().forEach { block ->
            val item = block.item
            CheckListItemCard(
                item = item,
                onToggleChecked = { onToggleItemChecked(item.id) },
                onTitleChange = { onItemTitleChange(item.id, it) },
                onActionChange = { onItemActionChange(item.id, it) },
                onDeleteItem = { onDeleteItem(item.id) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (showAddFields) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newItemTitle,
                        onValueChange = { newItemTitle = it },
                        label = { Text(stringResource(R.string.checklistsectioneditor_outlinedtf_item)) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = newItemAction,
                        onValueChange = { newItemAction = it },
                        label = { Text(stringResource(R.string.checklistsectioneditor_outlinedtf_action)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = {
                        if (newItemTitle.isNotBlank() || newItemAction.isNotBlank()) {
                            val wasAdded = onAddItem(newItemTitle, newItemAction)
                            if (wasAdded) {
                                newItemTitle = ""
                                newItemAction = ""
                                showAddFields = false
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.checklistsectioneditor_button_accept))
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAddFields = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.checklistsectioneditor_button_additem))
                }

                Button(
                    onClick = onAddSubsection,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Añadir Subsección")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
