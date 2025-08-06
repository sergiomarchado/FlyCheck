package com.sergiom.flycheck.ui.screens.b_editor.components.editor.utils

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.presentation.viewmodel.editor.TemplateEditorViewModel
import com.sergiom.flycheck.R

/**
 * Composable que ofrece controles para añadir un nuevo ítem o subsección
 * dentro de una sección ya existente.
 *
 * @param sectionId ID de la sección donde se añadirán los elementos.
 * @param viewModel ViewModel que gestiona las acciones de modificación del template.
 */
@Composable
fun SectionAddControls(
    sectionId: String,
    viewModel: TemplateEditorViewModel
) {
    // Controla la visibilidad del formulario de nuevo ítem
    var showAddFields by remember { mutableStateOf(false) }

    // Controla la visibilidad del formulario de nueva subsección
    var showSubForm by remember { mutableStateOf(false) }

    // Valores de entrada del nuevo ítem
    var newItemTitle by remember { mutableStateOf("") }
    var newItemAction by remember { mutableStateOf("") }

    // Valor de entrada del título de la nueva subsección
    var newSubsectionTitle by remember { mutableStateOf("") }

    // ➕ Formulario para añadir un ítem
    if (showAddFields) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = newItemTitle,
                onValueChange = { newItemTitle = it },
                label = { Text(stringResource(R.string.sectionaddscontrols_item_title)) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = newItemAction,
                onValueChange = { newItemAction = it },
                label = { Text(stringResource(R.string.sectionaddscontrols_item_action)) },
                modifier = Modifier.weight(1f)
            )
        }
        // Botón para confirmar la creación del ítem
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
            Text(stringResource(R.string.sectionaddscontrols_button_accept))
        }
    }

    // ➕ Formulario para añadir una subsección
    if (showSubForm) {
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newSubsectionTitle,
            onValueChange = { newSubsectionTitle = it },
            label = { Text(stringResource(R.string.sectionaddscontrols_subsection_title)) },
            modifier = Modifier.fillMaxWidth()
        )
        // Botones para cancelar o confirmar la creación de la subsección
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = {
                showSubForm = false
                newSubsectionTitle = ""
            }) {
                Text(stringResource(R.string.sectionaddscontrols_button_cancel))
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
                Text(stringResource(R.string.sectionaddscontrols_button_accept))
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    // 🔘 Botones para mostrar los formularios
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { showAddFields = true }, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.sectionaddscontrols_button_add_item))
        }
        Button(onClick = { showSubForm = true }, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.sectionaddscontrols_button_add_subsection))
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}


