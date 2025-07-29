package com.sergiom.flycheck.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListItem

@Composable
fun CheckListItemCard(
    item: CheckListItem,
    onDelete: () -> Unit,
    onToogleComplete: () -> Unit
) {
    /*
    * showDialog controla si se debe mostrar el diálogo de confirmación.
    * Usamos rememberSaveable para que se conserve incluso si giramos la pantalla.
    */
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Checkbox para marcar o desmarcar el ítem como completado
                Checkbox(
                    checked = item.completed,
                    onCheckedChange = { onToogleComplete() }
                )

                // Texto con tachado si el ítem está marcado como completado
                Text(
                    text = item.text,
                    style = if (item.completed)
                        MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough)
                    else
                        MaterialTheme.typography.bodyLarge
                )
            }

            // Botón de eliminar que muestra un diálogo de confirmación
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.checklistitemcard_icon_content_description)
                )
            }
        }

        // Diálogo de confirmación para eliminar el ítem
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.checklistitemcard_alertdialog_title)) },
                text = { Text(stringResource(R.string.checklistitemcard_alertdialog_text)) },
                confirmButton = {
                    TextButton(onClick = {
                        onDelete()
                        showDialog = false
                    }) {
                        Text(stringResource(R.string.checklistitemcard_alertdialog_option_yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.checklistitemcard_alertdialog_option_cancel))
                    }
                }
            )
        }
    }
}


