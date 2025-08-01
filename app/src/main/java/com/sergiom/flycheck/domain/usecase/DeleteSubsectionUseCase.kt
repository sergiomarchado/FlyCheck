package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel

class DeleteSubsectionUseCase {

    operator fun invoke(
        template: CheckListTemplateModel,
        sectionId: String,
        subsectionId: String
    ): CheckListTemplateModel {
        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.filterNot {
                        it is CheckListBlock.SubsectionBlock && it.subsection.id == subsectionId
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
