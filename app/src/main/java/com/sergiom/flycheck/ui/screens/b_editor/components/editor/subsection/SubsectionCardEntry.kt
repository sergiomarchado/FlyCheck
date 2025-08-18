package com.sergiom.flycheck.ui.screens.b_editor.components.editor.subsection

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.FlatBlock
import com.sergiom.flycheck.data.models.FlatBlockWithLocalIndex
import com.sergiom.flycheck.data.models.RenameTargetType
import com.sergiom.flycheck.viewmodel.editor.TemplateEditorViewModel

/**
 * Composable que representa una entrada visual de una subsección dentro del checklist.
 * Genera dinámicamente un `CheckListSubsectionTitleCard` con las acciones disponibles:
 * renombrar, eliminar, mover arriba y mover abajo.
 *
 * @param meta Información del bloque plano con índice local dentro de su sección.
 * @param viewModel ViewModel del editor que permite modificar el estado del checklist.
 * @param onRename Función que se invoca al solicitar renombrar una subsección.
 */
@Composable
fun SubsectionCardEntry(
    meta: FlatBlockWithLocalIndex,
    viewModel: TemplateEditorViewModel,
    onRename: (String, String, RenameTargetType) -> Unit
) {
    // Se asegura de que el bloque es de tipo Subsection
    val block = meta.block as FlatBlock.Subsection
    val subsection = block.subsectionBlock.subsection
    val sectionId = block.sectionId

    // Índice local dentro de la sección (usado para determinar posición relativa)
    val localIndex = meta.localIndex ?: return

    // Se calcula el índice máximo válido dentro de la sección para evitar desbordamientos
    val maxIndex = viewModel.uiState.value.blocks
        .find { it is CheckListBlock.SectionBlock && it.section.id == sectionId }
        ?.let { (it as CheckListBlock.SectionBlock).section.blocks.size - 1 } ?: return

    // Componente visual que representa la cabecera de una subsección
    CheckListSubsectionTitleCard(
        title = subsection.title,
        // RENOMBRAR
        onRenameClick = {
            onRename(subsection.id, subsection.title, RenameTargetType.SUBSECTION)
        },
        // ELIMINAR
        onDeleteClick = {
            viewModel.deleteSubsection(sectionId, subsection.id)
        },
        // MOVER HACIA ARRIBA (si no está en la primera posición)
        onMoveUp = {
            if (localIndex > 0) {
                viewModel.moveBlockInSection(sectionId, localIndex, localIndex - 1)
            }
        },
        // MOVER HACIA ABAJO (si no está en la última posición)
        onMoveDown = {
            if (localIndex < maxIndex) {
                viewModel.moveBlockInSection(sectionId, localIndex, localIndex + 1)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp) // Espaciado vertical entre subsecciones
    )
}

