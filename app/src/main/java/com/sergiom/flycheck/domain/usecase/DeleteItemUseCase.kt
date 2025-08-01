package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel

class DeleteItemUseCase {

    operator fun invoke(
        template: CheckListTemplateModel,
        sectionId: String,
        itemId: String
    ): CheckListTemplateModel {
        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.filterNot {
                        it is CheckListBlock.ItemBlock && it.item.id == itemId
                    }.toMutableList()
                )
                CheckListBlock.SectionBlock(updatedSection)
            } else {
                block
            }
        }

        return template.copy(blocks = updatedBlocks)
    }
}