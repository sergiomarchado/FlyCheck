package com.sergiom.flycheck.domain.player

import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListSection
import com.sergiom.flycheck.data.models.CheckListTemplateModel

data class ItemRef(
    val globalIndex: Int,
    val sectionIndex: Int,
    val subsectionPath: List<Int>, // índices de subsecciones anidadas (por si en futuro las usas)
    val itemBlock: CheckListBlock.ItemBlock,
    val section: CheckListSection,
    val subsectionTitles: List<String>
)

data class FlatPlayback(
    val template: CheckListTemplateModel,
    val items: List<ItemRef>,
    val sectionStartsAt: List<Int>, // índice global en items donde empieza cada sección
    val sectionTitles: List<String>
)

fun CheckListTemplateModel.flattenForPlayback(): FlatPlayback {
    val items = mutableListOf<ItemRef>()
    val sectionStarts = mutableListOf<Int>()
    val sectionTitles = mutableListOf<String>()

    blocks.forEachIndexed { sIdx, blk ->
        if (blk is CheckListBlock.SectionBlock) {
            sectionStarts += items.size
            sectionTitles += blk.section.title
            collectItems(
                nodes = blk.section.blocks,
                section = blk.section,
                sectionIndex = sIdx,
                subsectionPath = emptyList(),
                subsectionTitles = emptyList(),
                out = items
            )
        }
    }
    // asignar globalIndex
    val withIndexes = items.mapIndexed { i, ref -> ref.copy(globalIndex = i) }
    return FlatPlayback(
        template = this,
        items = withIndexes,
        sectionStartsAt = sectionStarts,
        sectionTitles = sectionTitles
    )
}

private fun collectItems(
    nodes: List<CheckListBlock>,
    section: CheckListSection,
    sectionIndex: Int,
    subsectionPath: List<Int>,
    subsectionTitles: List<String>,
    out: MutableList<ItemRef>
) {
    nodes.forEachIndexed { idx, b ->
        when (b) {
            is CheckListBlock.ItemBlock -> out += ItemRef(
                globalIndex = -1, // se fija luego
                sectionIndex = sectionIndex,
                subsectionPath = subsectionPath,
                itemBlock = b,
                section = section,
                subsectionTitles = subsectionTitles
            )
            is CheckListBlock.SubsectionBlock -> collectItems(
                nodes = b.subsection.blocks,
                section = section,
                sectionIndex = sectionIndex,
                subsectionPath = subsectionPath + idx,
                subsectionTitles = subsectionTitles + b.subsection.title,
                out = out
            )
            is CheckListBlock.SectionBlock -> {
                // En tu modelo, SectionBlock no debería aparecer dentro de otro, lo ignoramos por seguridad
            }
        }
    }
}
