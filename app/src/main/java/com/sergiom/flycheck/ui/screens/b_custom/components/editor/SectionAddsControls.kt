package com.sergiom.flycheck.ui.screens.b_custom.components.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel

@Composable
fun SectionAddControls(
    sectionId: String,
    viewModel: TemplateEditorViewModel
) {
    var showAddFields by remember { mutableStateOf(false) }
    var showSubForm by remember { mutableStateOf(false) }
    var newItemTitle by remember { mutableStateOf("") }
    var newItemAction by remember { mutableStateOf("") }
    var newSubsectionTitle by remember { mutableStateOf("") }

    if (showAddFields) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = newItemTitle,
                onValueChange = { newItemTitle = it },
                label = { Text("Título del ítem") },
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
                    val added = viewModel.addItem(sectionId, newItemTitle, newItemAction)
                    if (added) {
                        newItemTitle = ""
                        newItemAction = ""
                        showAddFields = false
                    }
                }
            },
        ) {
            Text("Aceptar")
        }
    }

    if (showSubForm) {
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newSubsectionTitle,
            onValueChange = { newSubsectionTitle = it },
            label = { Text("Título de la subsección") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = {
                showSubForm = false
                newSubsectionTitle = ""
            }) {
                Text("Cancelar")
            }
            Button(onClick = {
                if (newSubsectionTitle.isNotBlank()) {
                    val added = viewModel.addSubsection(sectionId, newSubsectionTitle)
                    if (added) {
                        showSubForm = false
                        newSubsectionTitle = ""
                    }
                }
            }) {
                Text("Aceptar")
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { showAddFields = true }, modifier = Modifier.weight(1f)) {
            Text("Añadir ítem")
        }
        Button(onClick = { showSubForm = true }, modifier = Modifier.weight(1f)) {
            Text("Añadir Subsección")
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}


