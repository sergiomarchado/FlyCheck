package com.sergiom.flycheck.util

import com.sergiom.flycheck.data.model.*
import com.sergiom.flycheck.data.model.FlatBlock.*

/**
 *  Transforma una lista jerárquica de bloques (CheckListBlock)
 *  en una lista aplanada de bloques (FlatBlock),conservando el índice local de cada ítem
 *  o subsección dentro de su sección.
 *
 *  Esto facilita mostrar la estructura en listas como LazyColumn, que requieren datos lineales.
 */
fun List<CheckListBlock>.toFlatBlockListWithIndices(): List<FlatBlockWithLocalIndex> {
    val result = mutableListOf<FlatBlockWithLocalIndex>()

    // Recorre todos los bloques del checklist (cada bloque puede ser una sección u otro tipo)
    for (block in this) {

        // Solo nos interesan las secciones para aplanarlas
        if (block is CheckListBlock.SectionBlock) {
            val section = block.section

            // Añade primero un bloque de cabecera de sección (sin índice porque no lo necesita)
            result += FlatBlockWithLocalIndex(SectionHeader(section.id, section))

            var itemIndex = 0  // Índice local de ítems y subsecciones dentro de la sección

            // Recorremos todos los bloques internos (ítems y subsecciones) de la sección
            section.blocks.forEach { inner ->
                when (inner) {
                    is CheckListBlock.ItemBlock -> {

                        // Añadimos un ítem plano con su índice local
                        result += FlatBlockWithLocalIndex(
                            Item(section.id, inner),
                            itemIndex++
                        )
                    }

                    is CheckListBlock.SubsectionBlock -> {
                        // Añadimos una subsección plana con su índice local
                        result += FlatBlockWithLocalIndex(
                            Subsection(section.id, inner),
                            itemIndex++
                        )
                    }

                    // De momento, no está contemplado que una sección contenga otra sección
                    is CheckListBlock.SectionBlock -> TODO()
                }
            }

            // Finalmente, se añade el control de "Añadir ítem/subsección" (sin índice)
            result += FlatBlockWithLocalIndex(AddControls(section.id))
        }
    }

    return result
}


