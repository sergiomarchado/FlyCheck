package com.sergiom.flycheck.ui.screens.b_custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiom.flycheck.components.CheckListHeader
import com.sergiom.flycheck.components.CheckListSectionEditor
import com.sergiom.flycheck.components.CheckListSectionHeader
import com.sergiom.flycheck.viewmodel.TemplateEditorViewModel

@Composable
fun CheckListEditorScreen(
    templateName: String,
    model: String,
    airline: String,
    includeLogo: Boolean,
    sectionCount: Int,
    viewModel: TemplateEditorViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.initializeTemplate(
            name = templateName,
            model = model,
            airline = airline,
            includeLogo = includeLogo,
            sectionCount = sectionCount
        )
    }

    val template by viewModel.uiState

    var editingSectionId by remember { mutableStateOf<String?>(null) }
    var editingSectionTitle by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        // Cabecera general
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            CheckListHeader(
                name = template.name,
                model = template.aircraftModel,
                airline = template.airline,
                includeLogo = template.includeLogo,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            template.sections.forEachIndexed { index, section ->

                stickyHeader(key = "header_$index") {
                    CheckListSectionHeader(
                        title = section.title,
                        onEditClick = {
                            editingSectionId = section.id
                            editingSectionTitle = section.title
                        }
                    )
                }

                item(key = "editor_$index") {
                    CheckListSectionEditor(
                        sectionId = section.id,
                        title = section.title,
                        items = section.items,
                        onTitleChange = { viewModel.updateSectionTitle(section.id, it) },
                        onAddItem = { title, action ->
                            viewModel.addItemToSection(section.id, title, action)
                        },
                        onToggleItemChecked = { itemId ->
                            viewModel.toggleItemCompleted(section.id, itemId)
                        },
                        onItemTitleChange = { itemId, newTitle ->
                            viewModel.updateItemTitle(section.id, itemId, newTitle)
                        },
                        onItemActionChange = { itemId, newAction ->
                            viewModel.updateItemAction(section.id, itemId, newAction)
                        }
                    )
                }
            }
        }

        // Diálogo de edición del título de sección
        if (editingSectionId != null) {
            AlertDialog(
                onDismissRequest = { editingSectionId = null },
                title = { Text("Editar título de sección") },
                text = {
                    OutlinedTextField(
                        value = editingSectionTitle,
                        onValueChange = { editingSectionTitle = it },
                        singleLine = true,
                        label = { Text("Nuevo título") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateSectionTitle(editingSectionId!!, editingSectionTitle)
                        editingSectionId = null
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingSectionId = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
