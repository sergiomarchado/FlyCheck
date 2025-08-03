package com.sergiom.flycheck.domain.usecase

import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListTemplateModel

// Caso de uso: eliminar una sección completa de una plantilla
class DeleteSectionUseCase {

    // El operador invoke permite llamar a esta clase como si fuera una función.
    operator fun invoke(
        template: CheckListTemplateModel, // 🧾 Estado actual de la plantilla
        sectionId: String
    ): CheckListTemplateModel {

        // Filtra todos los bloques, eliminando aquellos que sean secciones con el ID especificado
        val updatedBlocks = template.blocks.filterNot {
            it is CheckListBlock.SectionBlock && it.section.id == sectionId
        }

        // Devuelve una nueva instancia de la plantilla con los bloques actualizados
        // (sin la sección eliminada)
        return template.copy(blocks = updatedBlocks)
    }
}
