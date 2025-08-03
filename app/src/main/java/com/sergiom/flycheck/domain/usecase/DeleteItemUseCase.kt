package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel

// Caso de uso encargado de eliminar un ítem de una sección específica dentro de una plantilla
class DeleteItemUseCase {

    // Se invoca con el operador `invoke`, permitiendo usar la clase como una función
    operator fun invoke(
        template: CheckListTemplateModel, // Plantilla actual en la que se va a eliminar un ítem
        sectionId: String,                // ID de la sección que contiene el ítem
        itemId: String                    // ID del ítem a eliminar
    ): CheckListTemplateModel {

        // Recorremos todos los bloques principales (que pueden ser secciones)
        val updatedBlocks = template.blocks.map { block ->

            // Si el bloque actual es una sección y su ID coincide con el indicado...
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {

                //  Creamos una nueva sección con todos los bloques
                //  excepto el ítem que queremos eliminar
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.filterNot {
                        // Filtramos todos los bloques que NO sean el ítem con el ID que buscamos
                        it is CheckListBlock.ItemBlock && it.item.id == itemId
                    }.toMutableList()   // Convertimos de nuevo a lista mutable
                )
                // Devolvemos la sección modificada como un nuevo SectionBlock
                CheckListBlock.SectionBlock(updatedSection)
            } else {
                // Si no es la sección objetivo, la dejamos igual
                block
            }
        }

        // Devolvemos una nueva plantilla con la lista de bloques actualizada
        return template.copy(blocks = updatedBlocks)
    }
}