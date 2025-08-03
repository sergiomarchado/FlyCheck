package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListSection
import com.sergiom.flycheck.data.model.CheckListTemplateModel
import java.util.*

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



