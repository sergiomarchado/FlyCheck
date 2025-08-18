package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import com.sergiom.flycheck.domain.player.FlatPlayback
import com.sergiom.flycheck.domain.player.ItemRef

/**
 * Representa las filas que se renderizan en la lista de la checklist.
 *
 * - [SubsectionHeader]: cabecera que agrupa ítems dentro de una subsección.
 * - [CheckItem]: ítem operable de la checklist (con su índice global).
 *
 * @property key Clave estable usada en LazyColumn (mejora reciclado/animaciones).
 */
internal sealed interface DisplayerRowItem { val key: String

    /** Cabecera visual para el inicio de una subsección dentro de una sección. */
    data class SubsectionHeader(
        val title: String,
        override val key: String
    ) : DisplayerRowItem

    /** Ítem operable de checklist con índice global para onJumpToItem. */
    data class CheckItem(
        val globalIndex: Int,
        val ref: ItemRef,
        override val key: String
    ) : DisplayerRowItem
}

/**
 * Construye la lista de [DisplayerRowItem] (cabeceras + ítems) para una sección.
 *
 * 1) Filtra ítems de [FlatPlayback] por `sectionIndex`.
 * 2) Inserta [DisplayerRowItem.SubsectionHeader] cuando cambia la ruta de subsección.
 * 3) Inserta [DisplayerRowItem.CheckItem] por cada ítem conservando su índice global.
 *
 * Ejemplos de claves:
 * - "h:2:15" → header de subsección en sección 2, posición global 15.
 * - "i:15:ENG_START" → ítem con id `ENG_START` en posición global 15.
 */
internal fun buildSectionRows(
    flat: FlatPlayback?,
    sectionIndex: Int
): List<DisplayerRowItem> {
    if (flat == null) return emptyList()

    // Ítems que pertenecen a la sección indicada
    val raw = mutableListOf<Pair<Int, ItemRef>>()
    flat.items.forEachIndexed { gi, ref ->
        if (ref.sectionIndex == sectionIndex) raw += gi to ref
    }
    if (raw.isEmpty()) return emptyList()

    val result = mutableListOf<DisplayerRowItem>()
    var lastSubPath: List<String> = emptyList()

    for ((gi, ref) in raw) {
        // Cabecera si cambia la subsección y hay títulos disponibles
        if (ref.subsectionTitles != lastSubPath && ref.subsectionTitles.isNotEmpty()) {
            lastSubPath = ref.subsectionTitles
            ref.subsectionTitles.lastOrNull()?.let { title ->
                result += DisplayerRowItem.SubsectionHeader(
                    title = title,
                    key = "h:$sectionIndex:$gi"
                )
            }
        }
        // Ítem con clave estable (índice global + id)
        result += DisplayerRowItem.CheckItem(
            globalIndex = gi,
            ref = ref,
            key = "i:$gi:${ref.itemBlock.item.id}"
        )
    }
    return result
}
