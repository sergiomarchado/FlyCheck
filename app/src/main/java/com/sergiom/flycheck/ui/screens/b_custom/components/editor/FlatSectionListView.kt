package com.sergiom.flycheck.ui.screens.b_custom.components.editor

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.model.CheckListTemplateModel
import com.sergiom.flycheck.data.model.FlatBlock
import com.sergiom.flycheck.data.model.RenameTargetType
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.item.CheckListItemCard
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.section.CheckListSectionTitleCard
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.subsection.CheckListSubsectionTitleCard
import com.sergiom.flycheck.util.toFlatBlockListWithIndices

@Composable
fun FlatSectionListView(
    template: CheckListTemplateModel,
    viewModel: TemplateEditorViewModel,
    modifier: Modifier = Modifier,
    onRename: (String, String, RenameTargetType) -> Unit,
    onDelete: (String) -> Unit
) {
    val draggableIds by viewModel.draggableIds.collectAsState()
    val flatBlocksWithIndices = remember(template, draggableIds) {
        template.blocks.toFlatBlockListWithIndices()
    }
    val flatBlocks = flatBlocksWithIndices.map { it.block }

    var draggingItemId by remember { mutableStateOf<String?>(null) }
    val itemPositions = remember { mutableStateMapOf<String, Pair<Float, Float>>() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        itemsIndexed(flatBlocks, key = { _, block -> block.hashCode() }) { _, block ->
            when (block) {
                is FlatBlock.SectionHeader -> {
                    CheckListSectionTitleCard(
                        title = block.section.title,
                        onRenameClick = {
                            onRename(block.section.id, block.section.title, RenameTargetType.SECTION)
                        },
                        onDeleteClick = {
                            onDelete(block.section.id)
                        }
                    )
                }

                is FlatBlock.Subsection -> {
                    val subsectionId = block.subsectionBlock.subsection.id
                    val isDraggable = draggableIds.contains(subsectionId)

                    val subsectionCard: @Composable (Modifier) -> Unit = { subsectionModifier ->
                        CheckListSubsectionTitleCard(
                            title = block.subsectionBlock.subsection.title,
                            onRenameClick = {
                                onRename(subsectionId, block.subsectionBlock.subsection.title, RenameTargetType.SUBSECTION)
                            },
                            onDeleteClick = {
                                viewModel.deleteSubsection(block.sectionId, subsectionId)
                            },
                            onEnableDrag = {
                                draggingItemId = subsectionId
                                viewModel.enableDragFor(subsectionId)
                            },
                            modifier = subsectionModifier
                        )
                    }

                    if (isDraggable) {
                        DraggableItem(
                            itemId = subsectionId,
                            isDragging = draggingItemId == subsectionId,
                            onDragStart = { draggingItemId = subsectionId },
                            onDrag = { delta -> /* opcional */ },
                            onDragEnd = { offsetY ->
                                val draggedMeta = flatBlocksWithIndices.find {
                                    val block = it.block
                                    block is FlatBlock.Subsection &&
                                            block.subsectionBlock.subsection.id == subsectionId
                                } ?: return@DraggableItem

                                val draggedPos = itemPositions[subsectionId] ?: return@DraggableItem
                                val draggedCenterY = draggedPos.first + draggedPos.second / 2

                                val possibleTargets = flatBlocksWithIndices
                                    .filter {
                                        val block = it.block
                                        block is FlatBlock.Subsection &&
                                                block.subsectionBlock.subsection.id != subsectionId &&
                                                block.sectionId == draggedMeta.block.sectionId
                                    }

                                val closestTarget = possibleTargets.minByOrNull { meta ->
                                    val block = meta.block as? FlatBlock.Subsection ?: return@minByOrNull Float.MAX_VALUE
                                    val pos = itemPositions[block.subsectionBlock.subsection.id] ?: return@minByOrNull Float.MAX_VALUE
                                    val centerY = pos.first + pos.second / 2
                                    kotlin.math.abs(centerY - draggedCenterY)
                                } ?: return@DraggableItem

                                if (
                                    draggedMeta.localIndex != null &&
                                    closestTarget.localIndex != null &&
                                    draggedMeta.block.sectionId == closestTarget.block.sectionId &&
                                    draggedMeta.localIndex != closestTarget.localIndex
                                ) {
                                    viewModel.moveBlockInSection(
                                        sectionId = draggedMeta.block.sectionId,
                                        fromIndex = draggedMeta.localIndex,
                                        toIndex = closestTarget.localIndex
                                    )
                                }

                                draggingItemId = null
                                viewModel.disableDragFor(subsectionId)
                            },
                            registerItemPosition = { id, y, height ->
                                itemPositions[id] = y to height
                            }
                        ) { modifier, _, _, _ ->
                            subsectionCard(modifier.fillMaxWidth().padding(vertical = 4.dp))
                        }
                    } else {
                        subsectionCard(Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    }


                }

                is FlatBlock.Item -> {
                    val itemId = block.itemBlock.item.id
                    val isDraggable = draggableIds.contains(itemId)

                    val itemCard: @Composable (Modifier) -> Unit = { itemModifier ->
                        CheckListItemCard(
                            item = block.itemBlock.item,
                            onToggleChecked = {
                                viewModel.toggleItemCompleted(block.sectionId, block.itemBlock.item.id)
                            },
                            onTitleChange = {
                                viewModel.updateItem(
                                    block.sectionId,
                                    block.itemBlock.item.id,
                                    it,
                                    block.itemBlock.item.action
                                )
                            },
                            onActionChange = {
                                viewModel.updateItem(
                                    block.sectionId,
                                    block.itemBlock.item.id,
                                    block.itemBlock.item.title,
                                    it
                                )
                            },
                            onDeleteItem = {
                                viewModel.deleteItem(block.sectionId, block.itemBlock.item.id)
                            },
                            onEnableDrag = {
                                draggingItemId = itemId
                                viewModel.enableDragFor(itemId)
                            },
                            showDragHandle = isDraggable,
                            modifier = itemModifier,
                        )
                    }

                    if (isDraggable) {
                        DraggableItem(
                            itemId = itemId,
                            isDragging = draggingItemId == itemId,
                            onDragStart = { draggingItemId = itemId },
                            onDrag = {},
                            onDragEnd = { offsetY ->
                                val draggedMeta = flatBlocksWithIndices.find {
                                    it.block is FlatBlock.Item &&
                                            it.block.itemBlock.item.id == itemId
                                } ?: return@DraggableItem

                                val draggedPos = itemPositions[itemId] ?: return@DraggableItem
                                val draggedCenterY = draggedPos.first + draggedPos.second / 2

                                // Filtramos solo otros ítems dentro de la misma sección
                                val possibleTargets = flatBlocksWithIndices.filter {
                                    val block = it.block
                                    block is FlatBlock.Item &&
                                            block.itemBlock.item.id != itemId &&
                                            it.block.sectionId == draggedMeta.block.sectionId
                                }

                                val closestTarget = possibleTargets.minByOrNull { meta ->
                                    val block = meta.block as? FlatBlock.Item ?: return@minByOrNull Float.MAX_VALUE
                                    val pos = itemPositions[block.itemBlock.item.id] ?: return@minByOrNull Float.MAX_VALUE
                                    val centerY = pos.first + pos.second / 2
                                    kotlin.math.abs(centerY - draggedCenterY)
                                } ?: return@DraggableItem

                                if (
                                    draggedMeta.localIndex != null &&
                                    closestTarget.localIndex != null &&
                                    draggedMeta.block.sectionId == closestTarget.block.sectionId &&
                                    draggedMeta.localIndex != closestTarget.localIndex
                                ) {
                                    viewModel.moveBlockInSection(
                                        sectionId = draggedMeta.block.sectionId,
                                        fromIndex = draggedMeta.localIndex,
                                        toIndex = closestTarget.localIndex
                                    )
                                }

                                draggingItemId = null
                                viewModel.disableDragFor(itemId)
                            },
                            registerItemPosition = { id, y, height ->
                                itemPositions[id] = y to height
                            }
                        ) { modifier, _, _, _ ->
                            itemCard(modifier.fillMaxWidth().padding(vertical = 4.dp))
                        }
                    } else {
                        itemCard(Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    }
                }


                is FlatBlock.AddControls -> {
                    SectionAddControls(
                        sectionId = block.sectionId,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
