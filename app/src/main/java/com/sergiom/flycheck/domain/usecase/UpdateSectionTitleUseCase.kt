package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListTemplateModel

class UpdateSectionTitleUseCase {

    // Caso de uso: actualizar el título de una sección en una plantilla de checklist

    // El operador `invoke` permite llamar a la clase como si fuera una función.
    operator fun invoke(
        template: CheckListTemplateModel, // 🧾 Plantilla actual del checklist
        sectionId: String,                // 🔍 ID de la sección que queremos actualizar
        newTitle: String                  // ✏️ Nuevo título que queremos aplicar
    ): Result<CheckListTemplateModel> {

        //️ Eliminamos espacios innecesarios al principio y al final
        val trimmedTitle = newTitle.trim()

        // Validación 1: el nuevo título no puede estar vacío
        if (trimmedTitle.isBlank()) {
            return Result.failure(Exception(R.string.checklisteditorscreen_invalid_section_title_empty.toString()))
        }

        // Validación 2: el nuevo título no puede estar duplicado en otras secciones (case-insensitive)
        val titleExists = template.blocks.any { block ->
            block is CheckListBlock.SectionBlock &&
                    block.section.id != sectionId &&
                    block.section.title.equals(trimmedTitle, ignoreCase = true)
        }

        if (titleExists) {
            return Result.failure(Exception(R.string.checklisteditorscreen_invalid_section_title_duplicate.toString()))
        }


        // Si pasa las validaciones, actualizamos el título de la sección
        val updatedBlocks = template.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                // Reemplazamos solo el título de la sección
                block.copy(section = block.section.copy(title = trimmedTitle))
            } else {
                // Mantenemos el resto igual
                block
            }
        }

        // Devolvemos la nueva plantilla con los bloques actualizados
        return Result.success(template.copy(blocks = updatedBlocks))
    }
}
