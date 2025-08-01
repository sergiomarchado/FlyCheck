package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListSection
import com.sergiom.flycheck.data.model.CheckListTemplateModel
import java.util.*

class AddSubsectionUseCase {

    operator fun invoke(
        template: CheckListTemplateModel,
        parentSectionId: String,
        subsectionTitle: String
    ): Result<CheckListTemplateModel> {
        val trimmedTitle = subsectionTitle.trim()
        if (trimmedTitle.isEmpty()) {
            return Result.failure(IllegalArgumentException("${R.string.templateeditorviewmodel_warning_item_title_empty}"))
        }

        val newSubsection = CheckListSection(
            id = UUID.randomUUID().toString(),
            title = trimmedTitle,
            blocks = mutableListOf()
        )

        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == parentSectionId) {
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks + CheckListBlock.SubsectionBlock(newSubsection)
                )
                CheckListBlock.SectionBlock(updatedSection)
            } else {
                block
            }
        }

        return Result.success(template.copy(blocks = updatedBlocks))
    }
}


