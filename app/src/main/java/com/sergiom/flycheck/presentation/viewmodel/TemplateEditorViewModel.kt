package com.sergiom.flycheck.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListBlock
import com.sergiom.flycheck.data.model.CheckListSection
import com.sergiom.flycheck.data.model.CheckListTemplateModel
import com.sergiom.flycheck.domain.usecase.EditorUseCases
import com.sergiom.flycheck.ui.events.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TemplateEditorViewModel @Inject constructor(
    private val editorUseCases: EditorUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckListTemplateModel())
    val uiState: StateFlow<CheckListTemplateModel> = _uiState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    private val _draggableIds = MutableStateFlow<Set<String>>(emptySet())
    val draggableIds: StateFlow<Set<String>> = _draggableIds

    fun setTemplateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun setAircraftModel(model: String) {
        _uiState.update { it.copy(aircraftModel = model) }
    }

    fun setAirline(name: String) {
        _uiState.update { it.copy(airline = name) }
    }

    fun toggleIncludeLogo() {
        _uiState.update { it.copy(includeLogo = !it.includeLogo) }
    }

    fun addItem(sectionId: String, title: String, action: String): Boolean {
        val current = _uiState.value
        val result = editorUseCases.addItem(current, sectionId, title, action)

        return result.fold(
            onSuccess = { updatedTemplate ->
                _uiState.value = updatedTemplate
                true
            },
            onFailure = { throwable ->
                // Convertimos el mensaje del Throwable (que era un StringRes como String) de vuelta a Int
                val resId = throwable.message?.toIntOrNull()
                if (resId != null) {
                    showToast(resId)
                }
                false
            }
        )
    }

    fun addSubsection(sectionId: String, title: String): Boolean {
        val current = _uiState.value
        val result = editorUseCases.addSubsection(current, sectionId, title)

        result.onSuccess { updatedTemplate ->
            _uiState.value = updatedTemplate
            return true
        }.onFailure { error ->
            showToast(error.message?.toIntOrNull() ?: R.string.generic_error)
        }

        return false
    }

    fun deleteItem(sectionId: String, itemId: String) {
        val updated = editorUseCases.deleteItem(_uiState.value, sectionId, itemId)
        _uiState.value = updated
    }

    fun deleteSection(sectionId: String) {
        val updated = editorUseCases.deleteSection(_uiState.value, sectionId)
        _uiState.value = updated
    }

    fun deleteSubsection(sectionId: String, subsectionId: String) {
        val updated = editorUseCases.deleteSubsection(_uiState.value, sectionId, subsectionId)
        _uiState.value = updated
    }

    fun updateItem(sectionId: String, itemId: String, newTitle: String, newAction: String) {
        if (newTitle.isBlank()) {
            showToast(R.string.error_title_cannot_be_empty)
            return
        }

        if (newAction.isBlank()) {
            showToast(R.string.error_action_cannot_be_empty)
            return
        }

        val currentTemplate = _uiState.value

        val updatedBlocks = currentTemplate.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                val updatedSection = editorUseCases.updateItem(
                    block.section,
                    itemId,
                    newTitle,
                    newAction
                )
                CheckListBlock.SectionBlock(updatedSection)
            } else {
                block
            }
        }

        _uiState.value = currentTemplate.copy(blocks = updatedBlocks)
    }

    fun toggleItemCompleted(sectionId: String, itemId: String) {
        val updated = editorUseCases.toggleItemCompletion(_uiState.value, sectionId, itemId)
        _uiState.value = updated
    }

    fun updateSectionTitle(sectionId: String, newTitle: String): Boolean {
        val result = editorUseCases.updateSectionTitle(_uiState.value, sectionId, newTitle)

        result.onSuccess { updated ->
            _uiState.value = updated
            return true
        }.onFailure { exception ->
            exception.message?.toIntOrNull()?.let { resId ->
                showToast(resId)
            }
        }

        return false
    }

    fun updateSubsectionTitle(subsectionId: String, newTitle: String): Boolean {
        val result = editorUseCases.updateSubsectionTitle(_uiState.value, subsectionId, newTitle)

        result.onSuccess { updated ->
            _uiState.value = updated
            return true
        }.onFailure { exception ->
            exception.message?.toIntOrNull()?.let { resId ->
                showToast(resId)
            }
        }

        return false
    }

    fun initializeTemplate(
        name: String,
        model: String,
        airline: String,
        includeLogo: Boolean,
        sectionCount: Int
    ) {
        if (_uiState.value.blocks.isNotEmpty()) return

        val sections = List(sectionCount) { index ->
            CheckListSection(title = "Section ${index + 1}")
        }

        val blocks = sections.map { CheckListBlock.SectionBlock(it) }

        _uiState.value = CheckListTemplateModel(
            name = name,
            aircraftModel = model,
            airline = airline,
            includeLogo = includeLogo,
            blocks = blocks
        )
    }

    private fun showToast(resId: Int) {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowToast(resId))
        }
    }

    fun moveBlockInSection(sectionId: String, fromIndex: Int, toIndex: Int) {
        println("🟢 moveBlockInSection: de $fromIndex a $toIndex en sección $sectionId")
        val updatedBlocks = _uiState.value.blocks.map { block ->
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {
                val mutable = block.section.blocks.toMutableList()
                if (fromIndex in mutable.indices && toIndex in 0..mutable.size) {
                    val movedItem = mutable.removeAt(fromIndex)
                    mutable.add(toIndex, movedItem)
                }
                val updatedSection = block.section.copy(blocks = mutable)
                CheckListBlock.SectionBlock(updatedSection)
            } else {
                block
            }
        }
        _uiState.value = _uiState.value.copy(blocks = updatedBlocks)
    }

    fun enableDragFor(id: String) {
        println("🧲 Habilitando modo mover para ID: $id")
        _draggableIds.update { it + id }
    }

    fun disableDragFor(id: String) {
        _draggableIds.update { it - id }
    }

    fun clearDraggable() {
        _draggableIds.value = emptySet()
    }
}