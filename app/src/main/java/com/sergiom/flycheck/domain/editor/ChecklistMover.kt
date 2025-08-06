package com.sergiom.flycheck.domain.editor

import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListTemplateModel

class ChecklistMover {

    fun moveBlockInSection(
        template: CheckListTemplateModel,
        sectionId: String,
        fromIndex: Int,
        toIndex: Int
    ): CheckListTemplateModel {
        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                val originalList = block.section.blocks.toMutableList()

                if (fromIndex in originalList.indices && toIndex in 0..originalList.size) {
                    val movedItem = originalList.removeAt(fromIndex)
                    originalList.add(toIndex, movedItem)
                }

                val updatedSection = block.section.copy(blocks = originalList)
                CheckListBlock.SectionBlock(updatedSection)
            } else {
                block
            }
        }

        return template.copy(blocks = updatedBlocks)
    }
}
