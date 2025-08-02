package com.sergiom.flycheck.ui.screens.b_custom.components.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.model.*
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.item.CheckListItemCard
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.section.CheckListSectionTitleCard
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.subsection.CheckListSubsectionTitleCard
import com.sergiom.flycheck.util.toFlatBlockListWithIndices
import com.sergiom.flycheck.data.model.RenameTargetType

@Composable
fun FlatSectionListView(
    template: CheckListTemplateModel,
    viewModel: TemplateEditorViewModel,
    modifier: Modifier = Modifier,
    onRename: (String, String, RenameTargetType) -> Unit,
    onDelete: (String) -> Unit
) {
    val flatBlocksWithIndices = remember(template) {
        template.blocks.toFlatBlockListWithIndices()
    }

    val flatBlocks = flatBlocksWithIndices.map { it.block }

    var draggingItemId by remember { mutableStateOf<String?>(null) }
    val itemPositions = remember { mutableStateMapOf<String, Pair<Float, Float>>() }
    val dragEnabledItems = remember { mutableStateListOf<String>() }

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
                            onRename(
                                block.section.id,
                                block.section.title,
                                RenameTargetType.SECTION // ✅ sección
                            )
                        },
                        onDeleteClick = {
                            onDelete(block.section.id)
                        }
                    )
                }

                is FlatBlock.Subsection -> {
                    CheckListSubsectionTitleCard(
                        title = "↳ ${block.subsectionBlock.subsection.title}",
                        onRenameClick = {
                            onRename(
                                block.subsectionBlock.subsection.id,
                                block.subsectionBlock.subsection.title,
                                RenameTargetType.SUBSECTION // ✅ subsección
                            )
                        },
                        onDeleteClick = {
                            viewModel.deleteSubsection(
                                block.sectionId,
                                block.subsectionBlock.subsection.id
                            )
                        }
                    )
                }

                is FlatBlock.Item -> {
                    val itemId = block.itemBlock.item.id
                    val isDraggable = dragEnabledItems.contains(itemId)

                    val itemCard: @Composable () -> Unit = {
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
                                dragEnabledItems.add(itemId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }

                    if (isDraggable) {
                        DraggableItem(
                            itemId = itemId,
                            isDragging = draggingItemId == itemId,
                            onDragStart = { draggingItemId = itemId },
                            onDrag = { /* noop */ },
                            onDragEnd = {
                                val draggedMeta = flatBlocksWithIndices.find {
                                    it.block is FlatBlock.Item && it.block.itemBlock.item.id == itemId
                                } ?: return@DraggableItem

                                val draggedPos = itemPositions[itemId] ?: return@DraggableItem
                                val draggedCenterY = draggedPos.first + draggedPos.second / 2

                                val targetEntry = itemPositions
                                    .filterKeys { it != itemId }
                                    .minByOrNull { (_, pos) ->
                                        kotlin.math.abs((pos.first + pos.second / 2) - draggedCenterY)
                                    }

                                val targetId = targetEntry?.key ?: return@DraggableItem
                                val targetMeta = flatBlocksWithIndices.find {
                                    it.block is FlatBlock.Item && it.block.itemBlock.item.id == targetId
                                } ?: return@DraggableItem

                                if (
                                    draggedMeta.localIndex != null &&
                                    targetMeta.localIndex != null &&
                                    draggedMeta.block.sectionId == targetMeta.block.sectionId &&
                                    draggedMeta.localIndex != targetMeta.localIndex
                                ) {
                                    viewModel.moveBlockInSection(
                                        sectionId = draggedMeta.block.sectionId,
                                        fromIndex = draggedMeta.localIndex,
                                        toIndex = targetMeta.localIndex
                                    )
                                }

                                draggingItemId = null
                                dragEnabledItems.remove(itemId)
                            },
                            registerItemPosition = { id, y, height ->
                                itemPositions[id] = y to height
                            }
                        ) {
                            itemCard()
                        }
                    } else {
                        itemCard()
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
