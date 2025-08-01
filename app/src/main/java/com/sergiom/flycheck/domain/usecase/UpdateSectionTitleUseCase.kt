package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel

class UpdateSectionTitleUseCase {

    operator fun invoke(
        template: CheckListTemplateModel,
        sectionId: String,
        newTitle: String
    ): Result<CheckListTemplateModel> {
        val trimmedTitle = newTitle.trim()

        if (trimmedTitle.isBlank()) {
            return Result.failure(Exception(R.string.checklisteditorscreen_invalid_section_title_empty.toString()))
        }

        // Comprobamos duplicados (ignorando el que se está editando)
        val titleExists = template.blocks.any { block ->
            block is CheckListBlock.SectionBlock &&
                    block.section.id != sectionId &&
                    block.section.title.equals(trimmedTitle, ignoreCase = true)
        }

        if (titleExists) {
            return Result.failure(Exception(R.string.checklisteditorscreen_invalid_section_title_duplicate.toString()))
        }

        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                block.copy(section = block.section.copy(title = trimmedTitle))
            } else {
                block
            }
        }

        return Result.success(template.copy(blocks = updatedBlocks))
    }
}
