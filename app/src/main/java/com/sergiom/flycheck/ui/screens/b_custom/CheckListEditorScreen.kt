package com.sergiom.flycheck.ui.screens.b_custom

import android.widget.Toast
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiom.flycheck.R
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

    val template by viewModel.uiState.collectAsState()

    var editingSectionId by remember { mutableStateOf<String?>(null) }
    var editingSectionTitle by remember { mutableStateOf("") }

    var deletingSectionId by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current


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
                        onRenameClick = {
                            editingSectionId = section.id
                            editingSectionTitle = section.title
                        },
                        onDeleteClick = {
                            deletingSectionId = section.id
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
                            val success = viewModel.addItemToSection(section.id, title, action)
                            // Si falla, los campos se mantienen visibles. Si tiene éxito, los limpia el propio Editor.
                            if (!success) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.checklisteditorscreen_invalid_item),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            success
                        },
                        onToggleItemChecked = { itemId ->
                            viewModel.toggleItemCompleted(section.id, itemId)
                        },
                        onItemTitleChange = { itemId, newTitle ->
                            viewModel.updateItemTitle(section.id, itemId, newTitle)
                        },
                        onItemActionChange = { itemId, newAction ->
                            viewModel.updateItemAction(section.id, itemId, newAction)
                        },
                        onDeleteItem = {itemId ->
                            viewModel.deleteItemFromSection(section.id, itemId)
                        }
                    )
                }
            }
        }

        // Diálogo para renombrar la sección
        if (editingSectionId != null) {
            AlertDialog(
                onDismissRequest = { editingSectionId = null },
                title = { Text(stringResource(R.string.checklisteditorscreen_dialog_title)) },
                text = {
                    OutlinedTextField(
                        value = editingSectionTitle,
                        onValueChange = { editingSectionTitle = it },
                        singleLine = true,
                        label = { Text(stringResource(R.string.checklisteditorscreen_dialog_outlined_newtitle)) }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val success = viewModel.updateSectionTitle(editingSectionId!!, editingSectionTitle)
                        if (success) {
                            editingSectionId = null
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.checklisteditorscreen_invalid_section_title),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text(stringResource(R.string.checklisteditorscreen_dialog_confirmbutton))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingSectionId = null }) {
                        Text(stringResource(R.string.checklisteditorscreen_dialog_dismissbutton))
                    }
                }
            )
        }

        // Diálogo para confirmar eliminación de sección
        if (deletingSectionId != null) {
            AlertDialog(
                onDismissRequest = { deletingSectionId = null },
                title = { Text(stringResource(R.string.checklisteditorscreen_deletesection_title)) },
                text = { Text(stringResource(R.string.checklisteditorscreen_deletesection_warning)) },
                confirmButton = {
                    TextButton(onClick = {
                        deletingSectionId?.let {
                            viewModel.deleteSection(it)
                            deletingSectionId = null
                        }
                    }) {
                        Text(stringResource(R.string.checklisteditorscreen_deletesection_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deletingSectionId = null }) {
                        Text(stringResource(R.string.checklisteditorscreen_deletesection_dismiss))
                    }
                }
            )
        }

    }
}
