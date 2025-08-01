package com.sergiom.flycheck.ui.screens.b_custom.components.editor.subsection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.model.CheckListBlock

@Composable
fun SubsectionEditor(
    parentSectionId: String,
    blocks: List<CheckListBlock>,
    showFormExternally: Boolean,
    onDismissForm: () -> Unit,
    onAddSubsection: (String) -> Boolean,
    onRenameSubsection: (String, String) -> Unit,
    onDeleteSubsection: (String) -> Unit
) {
    var newSubTitle by rememberSaveable(parentSectionId + "_newsubsection") { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Mostrar subsecciones existentes
        blocks.filterIsInstance<CheckListBlock.SubsectionBlock>().forEach { block ->
            val subsection = block.subsection
            CheckListSubsectionTitleCard(
                title = subsection.title,
                onRenameClick = { onRenameSubsection(subsection.id, subsection.title) },
                onDeleteClick = { onDeleteSubsection(subsection.id) }
            )
        }

        // 🟢 Formulario para nueva subsección
        if (showFormExternally) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newSubTitle,
                    onValueChange = { newSubTitle = it },
                    label = { Text("Título de la Subsección") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        newSubTitle = ""
                        onDismissForm()
                    }) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (newSubTitle.isNotBlank()) {
                                val added = onAddSubsection(newSubTitle)
                                if (added) {
                                    newSubTitle = ""
                                    onDismissForm()
                                }
                            }
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}
