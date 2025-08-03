package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListSection
import com.sergiom.flycheck.data.model.CheckListTemplateModel
import java.util.*

// Caso de uso responsable de añadir una subsección a una sección existente en la plantilla
class AddSubsectionUseCase {

    // Función principal que se puede invocar como si fuera una función normal
    operator fun invoke(
        template: CheckListTemplateModel,     // Plantilla actual sobre la que se quiere trabajar
        parentSectionId: String,              // ID de la sección "padre" donde se añadirá la subsección
        subsectionTitle: String               // Título de la nueva subsección
    ): Result<CheckListTemplateModel> {

        // Validación: el título no puede estar vacío
        val trimmedTitle = subsectionTitle.trim()
        if (trimmedTitle.isEmpty()) {
            return Result.failure(IllegalArgumentException("${R.string.templateeditorviewmodel_warning_item_title_empty}"))
        }

        // Crear la nueva subsección como un CheckListSection vacío
        val newSubsection = CheckListSection(
            id = UUID.randomUUID().toString(),  // Genera un ID único para la subsección
            title = trimmedTitle,
            blocks = mutableListOf()            // La subsección comienza vacía
        )

        // Recorremos todos los bloques de la plantilla
        val updatedBlocks = template.blocks.map { block ->

            // Si encontramos la sección padre, añadimos la nueva subsección a sus bloques
            if (block is CheckListBlock.SectionBlock && block.section.id == parentSectionId) {
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks + CheckListBlock.SubsectionBlock(newSubsection)
                )
                // Reemplazamos el bloque original con el bloque actualizado
                CheckListBlock.SectionBlock(updatedSection)
            } else {
                // Si no es la sección que buscamos, la dejamos sin cambios
                block
            }
        }

        // Devolvemos una copia actualizada de la plantilla con la nueva subsección añadida
        return Result.success(template.copy(blocks = updatedBlocks))
    }
}


