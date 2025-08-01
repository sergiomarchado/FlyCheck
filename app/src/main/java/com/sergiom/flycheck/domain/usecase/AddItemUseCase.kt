package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListItemModel
import com.sergiom.flycheck.data.model.CheckListTemplateModel

class AddItemUseCase {

    operator fun invoke(
        template: CheckListTemplateModel,
        sectionId: String,
        title: String,
        action: String
    ): Result<CheckListTemplateModel> {
        val trimmedTitle = title.trim()

        if (trimmedTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("${R.string.templateeditorviewmodel_warning_item_title_empty}"))
        }

        val sectionIndex = template.blocks.indexOfFirst {
            it is CheckListBlock.SectionBlock && it.section.id == sectionId
        }

        if (sectionIndex == -1) {
            return Result.failure(IllegalArgumentException("${R.string.templateeditorviewmodel_warning_section_not_found}"))
        }

        val block = template.blocks[sectionIndex] as CheckListBlock.SectionBlock
        val section = block.section

        val alreadyExists = section.blocks.any {
            it is CheckListBlock.ItemBlock &&
                    it.item.title.equals(trimmedTitle, ignoreCase = true)
        }

        if (alreadyExists) {
            return Result.failure(IllegalArgumentException(R.string.templateeditorviewmodel_warning_item_title_empty.toString()))
        }

        val newItem = CheckListItemModel(
            title = trimmedTitle,
            action = action
        )

        val updatedSection = section.copy(
            blocks = section.blocks + CheckListBlock.ItemBlock(newItem)
        )

        val updatedBlocks = template.blocks.toMutableList().apply {
            this[sectionIndex] = CheckListBlock.SectionBlock(updatedSection)
        }

        return Result.success(template.copy(blocks = updatedBlocks))
    }
}
