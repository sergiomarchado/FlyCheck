package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel

class DeleteSectionUseCase {

    operator fun invoke(
        template: CheckListTemplateModel,
        sectionId: String
    ): CheckListTemplateModel {
        val updatedBlocks = template.blocks.filterNot {
            it is CheckListBlock.SectionBlock && it.section.id == sectionId
        }

        return template.copy(blocks = updatedBlocks)
    }
}
