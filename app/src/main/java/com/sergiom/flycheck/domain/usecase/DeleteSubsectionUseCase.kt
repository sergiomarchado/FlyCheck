package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListTemplateModel

// Caso de uso encargado de eliminar una subsección dentro de una sección específica de la plantilla
class DeleteSubsectionUseCase {

    // Función que recibe:
    // - la plantilla actual (`template`)
    // - el ID de la sección padre (`sectionId`)
    // - el ID de la subsección que se desea eliminar (`subsectionId`)
    operator fun invoke(
        template: CheckListTemplateModel,
        sectionId: String,
        subsectionId: String
    ): CheckListTemplateModel {

        // 🔃 Recorremos todos los bloques de primer nivel en la plantilla
        val updatedBlocks = template.blocks.map { block ->
            // Buscamos el bloque que representa la sección indicada
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {

                // Dentro de esa sección, filtramos los bloques internos
                // para eliminar la subsección deseada
                val updatedSection = block.section.copy(
                    blocks = block.section.blocks.filterNot {
                        // Eliminamos cualquier bloque que sea una SubsectionBlock
                        // con el ID especificado
                        it is CheckListBlock.SubsectionBlock && it.subsection.id == subsectionId
                    }.toMutableList()  // Convertimos de nuevo a lista mutable
                )
                // Devolvemos una nueva SectionBlock con la sección ya actualizada
                CheckListBlock.SectionBlock(updatedSection)
            } else {
                // Si no es la sección que nos interesa, la dejamos sin cambios
                block
            }
        }

        // Devolvemos la plantilla con los bloques ya actualizados
        return template.copy(blocks = updatedBlocks)
    }
}
