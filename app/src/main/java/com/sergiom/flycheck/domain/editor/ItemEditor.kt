package com.sergiom.flycheck.domain.editor

import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListTemplateModel

class ItemEditor {

    fun updateItemInfo(
        template: CheckListTemplateModel,
        sectionId: String,
        itemId: String,
        infoTitle: String,
        infoBody: String
    ): CheckListTemplateModel {
        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.map { subBlock ->
                        if (subBlock is CheckListBlock.ItemBlock && subBlock.item.id == itemId) {
                            val updatedItem = subBlock.item.copy(
                                infoTitle = infoTitle,
                                infoBody = infoBody
                            )
                            CheckListBlock.ItemBlock(updatedItem)
                        } else subBlock
                    }
                )
                CheckListBlock.SectionBlock(updatedSection)
            } else block
        }

        return template.copy(blocks = updatedBlocks)
    }

    fun updateItemImage(
        template: CheckListTemplateModel,
        sectionId: String,
        itemId: String,
        imageUri: String,
        imageTitle: String = "",
        imageDescription: String = ""
    ): CheckListTemplateModel {
        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.map { subBlock ->
                        if (subBlock is CheckListBlock.ItemBlock && subBlock.item.id == itemId) {
                            val updatedItem = subBlock.item.copy(
                                imageUri = imageUri,
                                imageTitle = imageTitle.takeIf { it.isNotBlank() },
                                imageDescription = imageDescription.takeIf { it.isNotBlank() }
                            )
                            CheckListBlock.ItemBlock(updatedItem)
                        } else subBlock
                    }
                )
                CheckListBlock.SectionBlock(updatedSection)
            } else block
        }

        return template.copy(blocks = updatedBlocks)
    }

    fun toggleItemImportance(
        template: CheckListTemplateModel,
        sectionId: String,
        itemId: String
    ): CheckListTemplateModel {
        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.map { subBlock ->
                        if (subBlock is CheckListBlock.ItemBlock && subBlock.item.id == itemId) {
                            val newColor = if (subBlock.item.backgroundColorHex == "#FFF59D") {
                                "#D3D3D3"
                            } else {
                                "#FFF59D"
                            }
                            val updatedItem = subBlock.item.copy(backgroundColorHex = newColor)
                            CheckListBlock.ItemBlock(updatedItem)
                        } else subBlock
                    }
                )
                CheckListBlock.SectionBlock(updatedSection)
            } else block
        }

        return template.copy(blocks = updatedBlocks)
    }

    fun toggleItemCompleted(
        template: CheckListTemplateModel,
        sectionId: String,
        itemId: String
    ): CheckListTemplateModel {
        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.map { subBlock ->
                        if (subBlock is CheckListBlock.ItemBlock && subBlock.item.id == itemId) {
                            val updatedItem = subBlock.item.copy(
                                completed = !subBlock.item.completed
                            )
                            CheckListBlock.ItemBlock(updatedItem)
                        } else subBlock
                    }
                )
                CheckListBlock.SectionBlock(updatedSection)
            } else block
        }

        return template.copy(blocks = updatedBlocks)
    }

    fun updateItem(
        template: CheckListTemplateModel,
        sectionId: String,
        itemId: String,
        newTitle: String,
        newAction: String
    ): CheckListTemplateModel {
        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.map { subBlock ->
                        if (subBlock is CheckListBlock.ItemBlock && subBlock.item.id == itemId) {
                            val updatedItem = subBlock.item.copy(
                                title = newTitle,
                                action = newAction
                            )
                            CheckListBlock.ItemBlock(updatedItem)
                        } else subBlock
                    }
                )
                CheckListBlock.SectionBlock(updatedSection)
            } else block
        }

        return template.copy(blocks = updatedBlocks)
    }

}
