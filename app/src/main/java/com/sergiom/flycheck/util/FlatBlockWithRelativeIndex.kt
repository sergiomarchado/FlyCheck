package com.sergiom.flycheck.util

import com.sergiom.flycheck.data.model.*
import com.sergiom.flycheck.data.model.FlatBlock.*

fun List<CheckListBlock>.toFlatBlockListWithIndices(): List<FlatBlockWithLocalIndex> {
    val result = mutableListOf<FlatBlockWithLocalIndex>()

    for (block in this) {
        if (block is CheckListBlock.SectionBlock) {
            val section = block.section
            result += FlatBlockWithLocalIndex(SectionHeader(section.id, section))

            var itemIndex = 0

            section.blocks.forEach { inner ->
                when (inner) {
                    is CheckListBlock.ItemBlock -> {
                        result += FlatBlockWithLocalIndex(
                            Item(section.id, inner),
                            itemIndex++
                        )
                    }

                    is CheckListBlock.SubsectionBlock -> {
                        result += FlatBlockWithLocalIndex(
                            Subsection(section.id, inner),
                            itemIndex++
                        )
                    }

                    is CheckListBlock.SectionBlock -> TODO()
                }
            }

            result += FlatBlockWithLocalIndex(AddControls(section.id))
        }
    }

    return result
}


