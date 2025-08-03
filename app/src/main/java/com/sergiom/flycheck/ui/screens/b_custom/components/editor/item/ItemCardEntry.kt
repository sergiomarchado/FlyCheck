package com.sergiom.flycheck.ui.screens.b_custom.components.editor.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.model.FlatBlock
import com.sergiom.flycheck.data.model.FlatBlockWithLocalIndex
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel

/**
 * Composable que renderiza una tarjeta (CheckListItemCard) correspondiente a un ítem
 * dentro de una sección del checklist, con capacidad de editar, eliminar y mover.
 *
 * Este componente se usa dentro de listas planas (flat block list), y se basa
 * en los metadatos del bloque (`FlatBlockWithLocalIndex`) y en el ViewModel
 * para aplicar la lógica de actualización.
 */
@Composable
fun ItemCardEntry(
    meta: FlatBlockWithLocalIndex, // Información bloque (ítem) + índice relativo dentro de sección
    viewModel: TemplateEditorViewModel
) {
    // Se extrae el bloque como FlatBlock.Item (tras casting seguro)
    val block = meta.block as FlatBlock.Item

    // Datos del ítem a renderizar
    val item = block.itemBlock.item

    // ID de la sección a la que pertenece este ítem
    val sectionId = block.sectionId

    // Índice del ítem dentro de su sección. Si es nulo, se aborta.
    val localIndex = meta.localIndex ?: return

    // Se obtiene el índice máximo válido dentro de la sección actual (para limitar movimiento descendente)
    val maxIndex = viewModel.uiState.value.blocks
        .find {
            it is com.sergiom.flycheck.data.model.CheckListBlock.SectionBlock
                    && it.section.id == sectionId }

        ?.let {
            (it as com.sergiom.flycheck.data.model.CheckListBlock.SectionBlock)
                .section.blocks.size - 1
        } ?: return  // Si no se encuentra o no hay bloques, se aborta.

    // Render del componente visual del ítem,
    CheckListItemCard(
        item = item,

        // Marca/desmarca como completado
        onToggleChecked = {
            viewModel.toggleItemCompleted(sectionId, item.id)
        },
        // Cambia el título del ítem
        onTitleChange = {
            viewModel.updateItem(sectionId, item.id, it, item.action)
        },
        // Cambia texto de la acción del ítem
        onActionChange = {
            viewModel.updateItem(sectionId, item.id, item.title, it)
        },
        // Borra el ítem actual
        onDeleteItem = {
            viewModel.deleteItem(sectionId, item.id)
        },
        // Mueve el ítem hacia arriba, si no está ya en la primera posición
        onMoveUp = {
            if (localIndex > 0) {
                viewModel.moveBlockInSection(sectionId, localIndex, localIndex - 1)
            }
        },
        // Mueve el ítem hacia abajo, si no está ya en la última posición
        onMoveDown = {
            if (localIndex < maxIndex) {
                viewModel.moveBlockInSection(sectionId, localIndex, localIndex + 1)
            }
        },
        // Estilo del contenedor visual del ítem
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}


