package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel


class UpdateSubsectionTitleUseCase {
    operator fun invoke(
        template: CheckListTemplateModel,
        subsectionId: String,
        newTitle: String
    ): Result<CheckListTemplateModel> {
        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock) {
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.map { inner ->
                        if (inner is CheckListBlock.SubsectionBlock && inner.subsection.id == subsectionId) {
                            CheckListBlock.SubsectionBlock(inner.subsection.copy(title = newTitle))
                        } else inner
                    }
                )
                CheckListBlock.SectionBlock(updatedSection)
            } else block
        }

        return Result.success(template.copy(blocks = updatedBlocks))
    }
}