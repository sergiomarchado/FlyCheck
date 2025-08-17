package com.sergiom.flycheck.domain.repository

import com.sergiom.flycheck.data.models.ChecklistInfo
import com.sergiom.flycheck.data.models.CheckListTemplateModel

interface ChecklistRepository {
    suspend fun listChecklists(): List<ChecklistInfo>
    suspend fun loadChecklist(id: String): CheckListTemplateModel?
    suspend fun saveChecklist(id: String, template: CheckListTemplateModel)
    suspend fun renameChecklist(oldId: String, newId: String): Boolean
    suspend fun deleteChecklist(id: String): Boolean
}
