package com.sergiom.flycheck.ui.screens.b_custom.components.editor.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.FlatBlock
import com.sergiom.flycheck.data.model.FlatBlockWithLocalIndex
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.utils.ImageEditDialog
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.utils.InfoEditDialog

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

    // Estado para mostrar el diálogo de info y editarlo o solo mostrar
    var showInfoDialog by remember { mutableStateOf(false) }
    var showReadOnlyInfo by remember { mutableStateOf(false) }

    // Estado para mostrar el diálogo de añadir imagen info
    var showImageDialog by remember { mutableStateOf(false) }
    var showReadOnlyImage by remember { mutableStateOf(false) }

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
        onAddInfoClick = {
            showInfoDialog = true
        },
        onViewInfoClick = {showReadOnlyInfo = true},
        onAddImageClick = {
            showImageDialog = true
        },
        onViewImageClick = {
            showReadOnlyImage = true
        },
        onToggleImportant = {
            viewModel.toggleItemImportance(sectionId, item.id)
        },
        // Estilo del contenedor visual del ítem
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )

    // Ver solo
    if (showReadOnlyInfo) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showReadOnlyInfo = false },
            confirmButton = {
                TextButton(onClick = { showReadOnlyInfo = false }) {
                    Text(stringResource(R.string.itemcardentry_textbutton_close))
                }
            },
            title = {
                Text(item.infoTitle ?: stringResource(R.string.itemcardentry_title_information))
            },
            text = {
                Text(item.infoBody ?: stringResource(R.string.itemcardentry_body_no_content))
            }
        )
    }

// Editar
    if (showInfoDialog) {
        InfoEditDialog(
            currentTitle = item.infoTitle.orEmpty(),
            currentBody = item.infoBody.orEmpty(),
            onDismiss = { showInfoDialog = false },
            onConfirm = { newTitle, newBody ->
                viewModel.updateItemInfo(sectionId, item.id, newTitle, newBody)
                showInfoDialog = false
            }
        )
    }

    if (showImageDialog) {
        ImageEditDialog(
            currentTitle = item.imageTitle.orEmpty(),
            currentBody = item.imageDescription.orEmpty(),
            currentImageUri = item.imageUri,
            onDismiss = { showImageDialog = false },
            onConfirm = { title, body, uri ->
                viewModel.updateItemImage(sectionId, item.id, uri ?: "", title, body)
                showImageDialog = false
            }
        )
    }

    if (showReadOnlyImage) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showReadOnlyImage = false },
            confirmButton = {
                TextButton(onClick = { showReadOnlyImage = false }) {
                    Text(stringResource(R.string.itemcardentry_textbutton_close))
                }
            },
            title = {
                Text(item.imageTitle ?: stringResource(R.string.itemcardentry_title_image))
            },
            text = {
                Column {
                    item.imageDescription?.let {
                        Text(it)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    item.imageUri?.let { uri ->
                        androidx.compose.foundation.Image(
                            painter = coil3.compose.rememberAsyncImagePainter(uri),
                            contentDescription = stringResource(R.string.itemcardentry_image_item),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }
            }
        )
    }
}




