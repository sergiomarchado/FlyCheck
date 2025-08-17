package com.sergiom.flycheck.presentation.viewmodel.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.domain.repository.ChecklistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class ChecklistManagerViewModel @Inject constructor(
    private val repo: ChecklistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerUiState(isLoading = true))
    val uiState: StateFlow<ManagerUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ManagerEffect>()
    val effects: SharedFlow<ManagerEffect> = _effects.asSharedFlow()

    init { refresh() }

    /** Refresca la lista en background. */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repo.listChecklists() }
                .onSuccess { list ->
                    _uiState.update { it.copy(isLoading = false, items = list, error = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al listar") }
                }
        }
    }

    /** Carga un template por id. Emite mensaje si hay problema. */
    suspend fun loadChecklist(id: String): CheckListTemplateModel? =
        runCatching { repo.loadChecklist(id) }
            .onFailure {
                viewModelScope.launch { _effects.emit(ManagerEffect.ShowMessage("No se pudo cargar")) }
            }
            .getOrNull()

    /** Guarda (persiste) un template ya normalizado para que sus URIs file:// queden en disco. */
    fun saveNormalized(id: String, template: CheckListTemplateModel) {
        viewModelScope.launch {
            runCatching { repo.saveChecklist(id, template) }
                .onFailure {
                    _effects.emit(ManagerEffect.ShowMessage("No se pudo guardar la checklist normalizada"))
                }
        }
    }

    /** Elimina en background y refresca. */
    fun deleteChecklist(id: String) {
        viewModelScope.launch {
            val ok = runCatching { repo.deleteChecklist(id) }.getOrElse { false }
            if (!ok) _effects.emit(ManagerEffect.ShowMessage("No se pudo eliminar"))
            refresh()
        }
    }

    /** Renombra en background y refresca. */
    fun renameChecklist(id: String, newName: String) {
        viewModelScope.launch {
            val ok = runCatching { repo.renameChecklist(id, newName) }.getOrElse { false }
            if (!ok) _effects.emit(ManagerEffect.ShowMessage("No se pudo renombrar"))
            refresh()
        }
    }
}
