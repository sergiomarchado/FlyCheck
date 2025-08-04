package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListItemModel
import com.sergiom.flycheck.data.model.CheckListTemplateModel

// Caso de uso: se encarga de añadir un nuevo ítem a una sección específica.
class AddItemUseCase {

    operator fun invoke(
        template: CheckListTemplateModel,  // Plantilla actual sobre la que se quiere trabajar
        sectionId: String,                 // ID de la sección donde se añadirá el ítem
        title: String,                     // Título del nuevo ítem
        action: String,                     // Acción asociada al ítem
        infoTitle: String = "",             // Campo info title cuando se añade info adicional
        infoBody: String = "",               // Campo info string cuando se añade info adicional
        imageUri: String = "",
        imageTitle: String = "",
        imageDescription: String = ""
    ): Result<CheckListTemplateModel> {   // Retorna un Result: éxito o fallo con mensaje

        // Validación 1: El título no puede estar vacío
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "${R.string.templateeditorviewmodel_warning_item_title_empty}"
                )
            )
        }

        // Buscar el índice de la sección a la que se quiere añadir el ítem
        val sectionIndex = template.blocks.indexOfFirst {
            it is CheckListBlock.SectionBlock && it.section.id == sectionId
        }

        // Validación 2: Si no se encuentra la sección, devolvemos error
        if (sectionIndex == -1) {
            return Result.failure(
                IllegalArgumentException(
                    "${R.string.templateeditorviewmodel_warning_section_not_found}"
                )
            )
        }

        // Recuperar la sección encontrada
        val block = template.blocks[sectionIndex] as CheckListBlock.SectionBlock
        val section = block.section

        // Validación 3: Evitar ítems duplicados (por título, ignorando mayúsculas/minúsculas)
        val alreadyExists = section.blocks.any {
            it is CheckListBlock.ItemBlock &&
                    it.item.title.equals(trimmedTitle, ignoreCase = true)
        }

        if (alreadyExists) {
            return Result.failure(
                IllegalArgumentException(
                    R.string.templateeditorviewmodel_warning_item_title_empty.toString()
                )
            )
        }

        // Crear el nuevo ítem
        val newItem = CheckListItemModel(
            title = trimmedTitle,
            action = action,
            infoTitle = infoTitle.trim().ifBlank { null },
            infoBody = infoBody.trim().ifBlank { null },
            imageUri = imageUri.ifBlank { null },
            imageTitle = imageTitle.trim().ifBlank { null },
            imageDescription = imageDescription.trim().ifBlank { null }
        )

        // Crear una nueva sección con el nuevo ítem añadido al final
        val updatedSection = section.copy(
            blocks = section.blocks + CheckListBlock.ItemBlock(newItem)
        )

        // Reemplazar la sección antigua con la nueva en la plantilla
        val updatedBlocks = template.blocks.toMutableList().apply {
            this[sectionIndex] = CheckListBlock.SectionBlock(updatedSection)
        }

        // Devolver la plantilla actualizada como resultado exitoso
        return Result.success(template.copy(blocks = updatedBlocks))
    }
}
