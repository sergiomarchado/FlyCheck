// presentation/viewmodel/manager/ChecklistManagerViewModel.kt
package com.sergiom.flycheck.presentation.viewmodel.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergiom.flycheck.data.local.ChecklistInfo
import com.sergiom.flycheck.data.local.ChecklistManagerRepository
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistManagerViewModel @Inject constructor(
    private val repo: ChecklistManagerRepository
) : ViewModel() {

    private val _checklists = MutableStateFlow<List<ChecklistInfo>>(emptyList())
    val checklists: StateFlow<List<ChecklistInfo>> = _checklists

    init {
        refresh()
    }

    private fun refresh() {
        // Si listChecklists() fuera suspend → usar viewModelScope.launch { ... }
        _checklists.value = repo.listChecklists()
    }

    fun loadChecklist(id: String): CheckListTemplateModel? =
        repo.loadChecklist(id)

    fun deleteChecklist(id: String) {
        viewModelScope.launch {
            repo.deleteChecklist(id)
            refresh()
        }
    }

    fun renameChecklist(id: String, newName: String): Boolean {
        val ok = repo.renameChecklist(id, newName)
        if (ok) refresh()
        return ok
    }
}
