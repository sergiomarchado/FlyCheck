package com.sergiom.flycheck.viewmodel.player.index

import com.sergiom.flycheck.data.models.SectionSummary
import com.sergiom.flycheck.data.models.SubsectionSummary
import com.sergiom.flycheck.domain.player.FlatPlayback
import com.sergiom.flycheck.domain.player.ItemRef
import com.sergiom.flycheck.domain.player.ItemStatus
import com.sergiom.flycheck.ui.common.SUBSECTION_SEP

/**
 * Índice precalculado para acelerar cómputos por sección/subsección.
 * - sectionItems  i -> todos los índices globales (gi) que pertenecen a la sección i.
 * - subsectionItems i  breadcrumb  -> todos los gi de esa subsección dentro de la sección i.
 */
internal data class SectionIndex(
    val sectionItems: List<IntArray>,
    val subsectionItems: List<Map<String, IntArray>>
)

/** Construye el índice a partir del FlatPlayback (se llama al cargar plantilla). */
internal fun buildSectionIndex(flat: FlatPlayback): SectionIndex {
    val sectionCount = flat.sectionTitles.size
    val perSectionLists = MutableList(sectionCount) { ArrayList<Int>() }
    val perSectionSubs = MutableList(sectionCount) { HashMap<String, ArrayList<Int>>() }

    flat.items.forEachIndexed { gi, ref ->
        val sIdx = ref.sectionIndex
        perSectionLists[sIdx].add(gi)

        val breadcrumb = ref.subsectionTitles.joinToString(SUBSECTION_SEP)
        if (breadcrumb.isNotBlank()) {
            perSectionSubs[sIdx].getOrPut(breadcrumb) { ArrayList() }.add(gi)
        }
    }

    val sectionItems = perSectionLists.map { it.toIntArray() }
    val subsectionItems = perSectionSubs.map { map -> map.mapValues { (_, v) -> v.toIntArray() } }

    return SectionIndex(sectionItems = sectionItems, subsectionItems = subsectionItems)
}

/** Summary por sección usando el índice (rápido: sólo cuenta DONE en arrays ya filtrados). */
internal fun buildSectionSummaries(
    index: SectionIndex,
    items: List<ItemRef>,
    sectionTitles: List<String>,
    statuses: Map<String, ItemStatus>
): List<SectionSummary> {
    return index.sectionItems.mapIndexed { i, arr ->
        val total = arr.size
        var done = 0
        for (gi in arr) if (statuses[items[gi].itemBlock.item.id] == ItemStatus.DONE) done++
        if (done > total) done = total // defensivo
        SectionSummary(
            title = sectionTitles.getOrNull(i).orEmpty(),
            done = done,
            total = total
        )
    }
}

/** Summary de subsecciones dentro de una sección concreta. */
internal fun buildSubsectionSummaries(
    index: SectionIndex,
    items: List<ItemRef>,
    statuses: Map<String, ItemStatus>,
    sectionIndex: Int
): List<SubsectionSummary> {
    val map = index.subsectionItems.getOrNull(sectionIndex) ?: emptyMap()
    if (map.isEmpty()) return emptyList()

    return map.entries.map { (path, arr) ->
        val total = arr.size
        var done = 0
        for (gi in arr) if (statuses[items[gi].itemBlock.item.id] == ItemStatus.DONE) done++
        if (done > total) done = total // defensivo
        SubsectionSummary(
            breadcrumb = path,
            title = path.substringAfterLast(SUBSECTION_SEP, path),
            done = done,
            total = total,
            firstGlobalIndex = arr.minOrNull() ?: 0
        )
    }.sortedBy { it.firstGlobalIndex }
}
