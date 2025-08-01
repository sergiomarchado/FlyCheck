package com.sergiom.flycheck.ui.screens.b_custom.components.editor.subsection

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AddSubsectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var subsectionTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Subsección") },
        text = {
            OutlinedTextField(
                value = subsectionTitle,
                onValueChange = { subsectionTitle = it },
                label = { Text("Título de la subsección") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (subsectionTitle.isNotBlank()) {
                    onConfirm(subsectionTitle)
                    subsectionTitle = ""
                }
            }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text("Cancelar")
            }
        }
    )
}
