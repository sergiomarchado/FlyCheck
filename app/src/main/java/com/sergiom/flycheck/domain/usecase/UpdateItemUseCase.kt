package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListSection

class UpdateItemUseCase {

    operator fun invoke(
        section: CheckListSection,
        itemId: String,
        newTitle: String? = null,
        newAction: String? = null,
        newColorHex: String? = null,
        toggleCompleted: Boolean = false
    ): CheckListSection {
        val updatedBlocks = section.blocks.map { block ->

            // Si el bloque es un ItemBlock y coincide el ID, actualizamos el ítem
            if (block is CheckListBlock.ItemBlock && block.item.id == itemId) {
                val updatedItem = block.item.copy(
                    title = newTitle?.takeIf { it.isNotBlank() } ?: block.item.title,
                    action = newAction ?: block.item.action,
                    backgroundColorHex = newColorHex ?: block.item.backgroundColorHex,
                    completed = if (toggleCompleted) !block.item.completed else block.item.completed
                )
                CheckListBlock.ItemBlock(updatedItem)
            } else {
                block
            }
        }

        return section.copy(blocks = updatedBlocks)
    }
}
