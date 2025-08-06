package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListSection
import com.sergiom.flycheck.data.models.CheckListTemplateModel


/**
 * Caso de uso para añadir una nueva sección vacía al checklist.
 */
class AddSectionUseCase {
    operator fun invoke(template: CheckListTemplateModel): CheckListTemplateModel {
        val newSection = CheckListSection(
            title = "New Section",
            blocks = mutableListOf()
        )

        val updatedBlocks = template.blocks.toMutableList().apply {
            add(CheckListBlock.SectionBlock(newSection))
        }

        return template.copy(blocks = updatedBlocks)
    }
}



