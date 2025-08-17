package com.sergiom.flycheck.domain.player

import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListSection
import com.sergiom.flycheck.data.models.CheckListTemplateModel

data class ItemRef(
    val globalIndex: Int,
    val sectionIndex: Int,
    val subsectionPath: List<Int>,          // índices de subsecciones anidadas
    val itemBlock: CheckListBlock.ItemBlock,
    val section: CheckListSection,
    val subsectionTitles: List<String>      // breadcrumb legible
)

data class FlatPlayback(
    val template: CheckListTemplateModel,
    val items: List<ItemRef>,
    val sectionStartsAt: List<Int>,         // índice global donde empieza cada sección
    val sectionTitles: List<String>
)

/**
 * Aplana la plantilla en una lista lineal de ítems con info de sección/subsección.
 * Soporta subsecciones:
 *  - con hijos (recursivo y reset al volver)
 *  - sin hijos (cabecera): el path queda "pegajoso" para los ItemBlock hermanos siguientes.
 */
fun CheckListTemplateModel.flattenForPlayback(): FlatPlayback {
    val items = mutableListOf<ItemRef>()
    val sectionStarts = mutableListOf<Int>()
    val sectionTitles = mutableListOf<String>()

    // Solo secciones en raíz
    blocks.forEach { blk ->
        if (blk is CheckListBlock.SectionBlock) {
            sectionStarts += items.size
            sectionTitles += blk.section.title
            collectItems(
                nodes = blk.section.blocks,
                section = blk.section,
                sectionIndex = sectionTitles.lastIndex,
                subsectionPath = emptyList(),
                subsectionTitles = emptyList(),
                out = items
            )
        }
    }

    // Asignar globalIndex real
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
    // Path/títulos "pegajosos" en este nivel
    var currentPath = subsectionPath
    var currentTitles = subsectionTitles

    for (i in nodes.indices) {
        when (val b = nodes[i]) {
            is CheckListBlock.ItemBlock -> {
                out += ItemRef(
                    globalIndex = -1, // se fija luego
                    sectionIndex = sectionIndex,
                    subsectionPath = currentPath,
                    itemBlock = b,
                    section = section,
                    subsectionTitles = currentTitles
                )
            }

            is CheckListBlock.SubsectionBlock -> {
                val nextPath = currentPath + i
                val nextTitles = currentTitles + b.subsection.title

                if (b.subsection.blocks.isNotEmpty()) {
                    // Con hijos → procesar y al volver resetear al path base
                    collectItems(
                        nodes = b.subsection.blocks,
                        section = section,
                        sectionIndex = sectionIndex,
                        subsectionPath = nextPath,
                        subsectionTitles = nextTitles,
                        out = out
                    )
                    currentPath = subsectionPath
                    currentTitles = subsectionTitles
                } else {
                    // Cabecera sin hijos → a partir de aquí, pegajoso para los ítems hermanos
                    currentPath = nextPath
                    currentTitles = nextTitles
                }
            }

            is CheckListBlock.SectionBlock -> {
                // No debería haber otra sección anidada; se ignora por robustez
            }
        }
    }
}
