package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListSection

/* Caso de uso: actualizar un ítem dentro de una sección.

    Este caso de uso se encarga de modificar ciertas propiedades de un ítem
    (título, acción, color o estado de completado) dentro de una sección dada.*/
class UpdateItemUseCase {

    // El operador `invoke` permite usar la clase como si fuera una función.
    operator fun invoke(
        section: CheckListSection,         // Sección que contiene los ítems a revisar
        itemId: String,                    // ID del ítem que queremos actualizar
        newTitle: String? = null,          // Nuevo título opcional (si no se pasa, se conserva el actual)
        newAction: String? = null,         // Nueva acción opcional
        newColorHex: String? = null,       // Nuevo color opcional en formato hexadecimal
        toggleCompleted: Boolean = false   // Alternar el estado de completado si es `true`
    ): CheckListSection {

        // Recorremos todos los bloques de la sección
        val updatedBlocks = section.blocks.map { block ->

            // Si encontramos un bloque de tipo ItemBlock con el ID especificado
            if (block is CheckListBlock.ItemBlock && block.item.id == itemId) {

                // Creamos una copia del ítem con los cambios correspondientes
                val updatedItem = block.item.copy(
                    title = newTitle ?: block.item.title,
                    action = newAction ?: block.item.action,
                    backgroundColorHex = newColorHex ?: block.item.backgroundColorHex,
                    completed = if (toggleCompleted) !block.item.completed else block.item.completed
                )
                // Sustituimos el bloque antiguo por uno nuevo con el ítem actualizado
                CheckListBlock.ItemBlock(updatedItem)
            } else {
                // Si no coincide o no es un ítem, lo dejamos tal cual
                block
            }
        }

        // Devolvemos una nueva sección con los bloques actualizados
        return section.copy(blocks = updatedBlocks)
    }
}
