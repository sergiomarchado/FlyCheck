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
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel principal del editor de plantillas de checklist.
 * Administra el estado, lógica de negocio y comunicación con los casos de uso.
 */
@HiltViewModel
class TemplateEditorViewModel @Inject constructor(

    // Casos de uso con lógica de negocio
    private val editorUseCases: EditorUseCases
) : ViewModel() {

    // Estado observable de la UI
    private val _uiState = MutableStateFlow(CheckListTemplateModel())
    val uiState: StateFlow<CheckListTemplateModel> = _uiState

    // 📢 Flujo de eventos de UI (como mostrar un Toast)
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow


    // ➕ Añadir un ítem a una sección existente del checklist.
    // Retorna true si la operación fue exitosa, false si falló.
    fun addItem(sectionId: String, title: String, action: String): Boolean {
        // Obtiene el estado actual del template
        val current = _uiState.value

        // Llama al caso de uso para intentar añadir un nuevo ítem en la sección indicada.
        // Devuelve un Result<CheckListTemplateModel> que puede ser éxito o error.
        val result = editorUseCases.addItem(current, sectionId, title, action)


        return result.fold(
            // ✅ Éxito: actualiza el estado con el nuevo template y retorna true
            onSuccess = { updatedTemplate ->
                _uiState.value = updatedTemplate
                true
            },
            // ❌ Error: intenta convertir el mensaje del error (String) a un ID de recurso (Int)
            onFailure = { throwable ->
                val resId = throwable.message?.toIntOrNull()

                // Si el mensaje era un recurso válido, muestra un Toast con ese mensaje
                if (resId != null) {
                    showToast(resId)
                }
                // Retorna false indicando que la operación falló
                false
            }
        )
    }


    // ➕ Añadir una nueva subsección dentro de una sección existente del checklist.
    // Retorna true si se añadió correctamente, false si ocurrió algún error.
    fun addSubsection(sectionId: String, title: String): Boolean {
        // Obtiene el estado actual del template desde el StateFlow
        val current = _uiState.value

        // Llama al caso de uso encargado de gestionar la lógica de añadir la subsección
        val result = editorUseCases.addSubsection(current, sectionId, title)

        // ✅ Si fue exitoso, actualiza el estado global con la nueva plantilla modificada
        result.onSuccess { updatedTemplate ->
            _uiState.value = updatedTemplate
            return true

        // ❌ Si ocurrió un error, intenta mostrar un mensaje mediante Toast
        }.onFailure { error ->
            showToast(error.message?.toIntOrNull() ?: R.string.generic_error)
        }

        return false
    }

    fun deleteItem(sectionId: String, itemId: String) {
        val updated =
            editorUseCases.deleteItem(_uiState.value, sectionId, itemId)
        _uiState.value = updated
    }

    fun deleteSection(sectionId: String) {
        val updated =
            editorUseCases.deleteSection(_uiState.value, sectionId)
        _uiState.value = updated
    }

    fun deleteSubsection(sectionId: String, subsectionId: String) {
        val updated =
            editorUseCases.deleteSubsection(_uiState.value, sectionId, subsectionId)
        _uiState.value = updated
    }

    // ✏️ Actualiza el título y la acción de un ítem existente dentro de una sección.
    // No retorna nada, pero actualiza el estado global si la operación tiene éxito.
    fun updateItem(sectionId: String, itemId: String, newTitle: String, newAction: String) {
        // ❗ Validación: el título no puede estar vacío
        if (newTitle.isBlank()) {
            showToast(R.string.error_title_cannot_be_empty)
            return
        }

        // ❗ Validación: la acción tampoco puede estar vacía
        if (newAction.isBlank()) {
            showToast(R.string.error_action_cannot_be_empty)
            return
        }

        // Obtiene el estado actual del template
        val currentTemplate = _uiState.value

        // Recorre todos los bloques (secciones) del template
        val updatedBlocks = currentTemplate.blocks.map { block ->

            // Busca la sección que contiene el ítem que queremos actualizar
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {

                // Utiliza el caso de uso para obtener la sección actualizada con el ítem modificado
                val updatedSection = editorUseCases.updateItem(
                    block.section,
                    itemId,
                    newTitle,
                    newAction
                )
                // Crea un nuevo bloque con la sección modificada
                CheckListBlock.SectionBlock(updatedSection)
            } else {
                // Si no es la sección que buscamos, la deja sin cambios
                block
            }
        }

        // Actualiza el estado global del template con la nueva lista de bloques
        _uiState.value = currentTemplate.copy(blocks = updatedBlocks)
    }

    fun toggleItemCompleted(sectionId: String, itemId: String) {
        val updated =
            editorUseCases.toggleItemCompletion(_uiState.value, sectionId, itemId)
        _uiState.value = updated
    }

    fun updateSectionTitle(sectionId: String, newTitle: String): Boolean {
        val result =
            editorUseCases.updateSectionTitle(_uiState.value, sectionId, newTitle)

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
        val result =
            editorUseCases.updateSubsectionTitle(_uiState.value, subsectionId, newTitle)

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

    // Inicializa una nueva plantilla (CheckListTemplateModel) con valores iniciales
    fun initializeTemplate(
        name: String,
        model: String,
        airline: String,
        includeLogo: Boolean,
        sectionCount: Int
    ) {
        // Evita sobrescribir si la plantilla ya está cargada (útil al recomponer)
        if (_uiState.value.blocks.isNotEmpty()) return

        // Genera una lista de secciones vacías con títulos "Section 1", "Section 2", etc.
        val sections = List(sectionCount) { index ->
            CheckListSection(title = "Section ${index + 1}")
        }

        // Convierte las secciones en bloques de tipo SectionBlock
        val blocks = sections.map { CheckListBlock.SectionBlock(it) }

        // Crea un nuevo estado para el template con todos los datos proporcionados
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

    // Mueve un bloque (ítem o subsección) dentro de una misma sección, de una posición a otra.
    fun moveBlockInSection(sectionId: String, fromIndex: Int, toIndex: Int) {

        // Recorre todos los bloques del template para encontrar la sección afectada
        val updatedBlocks = _uiState.value.blocks.map { block ->

            // Solo modifica si se trata de la sección que coincide con el ID
            if (block is CheckListBlock.SectionBlock && block.section.id == sectionId) {

                // Crea una copia mutable de los bloques internos de sección (ítems y subsecciones)
                val mutable = block.section.blocks.toMutableList()

                // Comprueba que los índices sean válidos antes de hacer el cambio
                if (fromIndex in mutable.indices && toIndex in 0..mutable.size) {

                    // Extrae el bloque desde su posición original
                    val movedItem = mutable.removeAt(fromIndex)
                    // Lo inserta en la nueva posición
                    mutable.add(toIndex, movedItem)
                }
                // Crea una copia nueva de la sección con los bloques ya reordenados
                val updatedSection = block.section.copy(blocks = mutable)

                // Devuelve la sección reempaquetada como un nuevo SectionBlock
                CheckListBlock.SectionBlock(updatedSection)
            } else {
                // Para el resto de secciones, no hace ningún cambio
                block
            }
        }
        // Actualiza el estado global con la nueva estructura de bloques
        _uiState.value = _uiState.value.copy(blocks = updatedBlocks)
    }

    fun addSection() {
        val updated = editorUseCases.addSectionUseCase(_uiState.value)
        _uiState.value = updated
    }

}