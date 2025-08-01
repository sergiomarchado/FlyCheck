package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel

class ToggleItemCompletionUseCase {

    operator fun invoke(
        template: CheckListTemplateModel,
        sectionId: String,
        itemId: String
    ): CheckListTemplateModel {
        val updatedBlocks = template.blocks.map { block ->
            when (block) {
                is CheckListBlock.SectionBlock -> {
                    if (block.section.id == sectionId) {
                        val updatedSection = block.section.copy(
                            blocks = block.section.blocks.map { innerBlock ->
                                if (innerBlock is CheckListBlock.ItemBlock &&
                                    innerBlock.item.id == itemId
                                ) {
                                    val toggledItem = innerBlock.item.copy(completed = !innerBlock.item.completed)
                                    CheckListBlock.ItemBlock(toggledItem)
                                } else {
                                    innerBlock
                                }
                            }
                        )
                        CheckListBlock.SectionBlock(updatedSection)
                    } else {
                        block
                    }
                }
                else -> block
            }
        }

        return template.copy(blocks = updatedBlocks)
    }
}

