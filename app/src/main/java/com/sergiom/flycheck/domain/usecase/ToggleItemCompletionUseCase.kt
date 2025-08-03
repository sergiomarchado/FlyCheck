package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel

// Caso de uso: alternar (toggle) el estado de completado de un ítem dentro de una sección
class ToggleItemCompletionUseCase {

    // operador `invoke` para permitir llamar a la instancia como si fuera una función
    operator fun invoke(
        template: CheckListTemplateModel, // Plantilla actual
        sectionId: String,                // ID de la sección donde está el ítem
        itemId: String                    // ID del ítem cuyo estado queremos alternar
    ): CheckListTemplateModel {

        // Recorremos todos los bloques de primer nivel en la plantilla
        val updatedBlocks = template.blocks.map { block ->
            when (block) {
                is CheckListBlock.SectionBlock -> {
                    // Solo nos interesa la sección cuyo ID coincide con el recibido
                    if (block.section.id == sectionId) {

                        // Creamos una nueva sección con los bloques actualizados
                        val updatedSection = block.section.copy(
                            blocks = block.section.blocks.map { innerBlock ->

                                // Si encontramos el ítem por su ID...
                                if (innerBlock is CheckListBlock.ItemBlock &&
                                    innerBlock.item.id == itemId
                                ) {
                                    // Alternamos el valor de `completed`
                                    val toggledItem = innerBlock.item.copy(completed = !innerBlock.item.completed)
                                    CheckListBlock.ItemBlock(toggledItem)
                                } else {
                                    // Si no es el ítem, lo dejamos tal cual
                                    innerBlock
                                }
                            }
                        )
                        // Devolvemos la sección con sus ítems modificados
                        CheckListBlock.SectionBlock(updatedSection)
                    } else {
                        // No es la sección que buscamos → no hacemos nada
                        block
                    }
                }
                // Si no es un bloque de tipo SectionBlock → se ignora
                else -> block
            }
        }

        // Devolvemos un nuevo estado del template con los bloques ya modificados
        return template.copy(blocks = updatedBlocks)
    }
}

