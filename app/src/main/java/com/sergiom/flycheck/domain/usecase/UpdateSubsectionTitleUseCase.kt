package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel

// Caso de uso: actualiza el título de una subsección concreta dentro de cualquier sección
class UpdateSubsectionTitleUseCase {
    // Permite que se pueda invocar como si fuera una función
    operator fun invoke(
        template: CheckListTemplateModel, // 🧾 Estado actual de la plantilla
        subsectionId: String,             // 🆔 ID de la subsección a modificar
        newTitle: String                  // ✏️ Nuevo título que queremos asignar
    ): Result<CheckListTemplateModel> {

        // Recorremos todos los bloques de nivel superior del checklist
        val updatedBlocks = template.blocks.map { block ->

            // Buscamos solo bloques de tipo sección
            if (block is CheckListBlock.SectionBlock) {

                // Dentro de la sección, recorremos sus bloques internos
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.map { inner ->

                        // Si encontramos una SubsectionBlock con el ID deseado, actualizamos su título
                        if (inner is CheckListBlock.SubsectionBlock && inner.subsection.id == subsectionId) {
                            CheckListBlock.SubsectionBlock(inner.subsection.copy(title = newTitle))
                        } else inner // El resto de bloques se mantienen sin cambios
                    }
                )
                // Devolvemos la sección con sus bloques posiblemente actualizados
                CheckListBlock.SectionBlock(updatedSection)
            } else block // Si no es una sección, se devuelve igual
        }

        // Devolvemos la plantilla con los bloques modificados
        return Result.success(template.copy(blocks = updatedBlocks))
    }
}