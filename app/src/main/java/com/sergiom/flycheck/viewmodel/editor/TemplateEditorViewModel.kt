package com.sergiom.flycheck.viewmodel.editor

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.models.CheckListBlock
import com.sergiom.flycheck.data.models.CheckListSection
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.domain.editor.ChecklistMover
import com.sergiom.flycheck.domain.editor.ItemEditor
import com.sergiom.flycheck.domain.export.TemplateExporter
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
    private val editorUseCases: EditorUseCases,

    // Clase Helper para la lógica de edición de items
    private val itemEditor: ItemEditor,

    // Clase Helper para la lógica de movimiento de items
    private val checklistMover: ChecklistMover,

    // Clase Helper para la lógica de exportación de la checklist
    private val templateExporter: TemplateExporter
) : ViewModel() {

    // Estado observable de la UI
    private val _uiState = MutableStateFlow(CheckListTemplateModel())
    val uiState: StateFlow<CheckListTemplateModel> = _uiState

    // 📢 Flujo de eventos de UI (como mostrar un Toast)
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow


    fun initializeTemplate(
        name: String,
        model: String,
        airline: String,
        includeLogo: Boolean,
        sectionCount: Int,
        logoUri: Uri?
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
            logoUri = logoUri,
            blocks = blocks
        )
    }


    // ➕ Añadir un ítem a una sección existente del checklist.
    // Retorna true si la operación fue exitosa, false si falló.
    fun addItem(
        sectionId: String,
        title: String,
        action: String,
        infoTitle: String = "",
        infoBody: String = ""
    ): Boolean {
        // Obtiene el estado actual del template
        val current = _uiState.value

        // Llama al caso de uso para intentar añadir un nuevo ítem en la sección indicada.
        // Devuelve un Result<CheckListTemplateModel> que puede ser éxito o error.
        val result =
            editorUseCases.addItem(
                template = current,
                sectionId = sectionId,
                title = title,
                action = action,
                infoTitle = infoTitle,
                infoBody = infoBody
            )


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


    /** Actualiza la información de título y cuerpo del díalo de info adicional de item.
    *
     * @param sectionId ID de la sección donde se encuentra el ítem.
     * @param itemId ID del ítem a actualizar.
     * @param infoTitle Nuevo título de la información adicional.
     * @param infoBody Nuevo contenido/cuerpo de la información adicional.
     */
    fun updateItemInfo(sectionId: String, itemId: String, infoTitle: String, infoBody: String) {
        _uiState.value = itemEditor.updateItemInfo(
            _uiState.value,
            sectionId,
            itemId,
            infoTitle,
            infoBody
        )
    }

    /**
     * Actualiza la imagen asociada a un ítem dentro de una sección específica.
     *
     * @param sectionId ID de la sección donde está el ítem.
     * @param itemId ID del ítem que se quiere modificar.
     * @param imageUri URI de la imagen seleccionada.
     * @param imageTitle Título opcional de la imagen.
     * @param imageDescription Descripción opcional de la imagen.
     */
    fun updateItemImage(
        sectionId: String,
        itemId: String,
        imageUri: String,
        imageTitle: String,
        imageDescription: String
    ) {
        _uiState.value = itemEditor.updateItemImage(
            _uiState.value,
            sectionId,
            itemId,
            imageUri,
            imageTitle,
            imageDescription
        )
    }

    fun toggleItemImportance(sectionId: String, itemId: String) {
        _uiState.value = itemEditor.toggleItemImportance(_uiState.value, sectionId, itemId)
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
        if (newTitle.isBlank()) {
            showToast(R.string.error_title_cannot_be_empty)
            return
        }

        if (newAction.isBlank()) {
            showToast(R.string.error_action_cannot_be_empty)
            return
        }

        _uiState.value = itemEditor.updateItem(
            _uiState.value,
            sectionId,
            itemId,
            newTitle,
            newAction
        )
    }

    fun toggleItemCompleted(sectionId: String, itemId: String) {
        _uiState.value = itemEditor.toggleItemCompleted(_uiState.value, sectionId, itemId)
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


    private fun showToast(resId: Int) {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowToast(resId))
        }
    }

    // Mueve un bloque (ítem o subsección) dentro de una misma sección, de una posición a otra.
    fun moveBlockInSection(sectionId: String, fromIndex: Int, toIndex: Int) {
        _uiState.value = checklistMover.moveBlockInSection(
            template = _uiState.value,
            sectionId = sectionId,
            fromIndex = fromIndex,
            toIndex = toIndex
        )
    }

    fun addSection() {
        val updated = editorUseCases.addSectionUseCase(_uiState.value)
        _uiState.value = updated
    }

    /**
     * Exporta la checklist actual como JSON en el almacenamiento privado de la app.
     * Esta opción NO abre menú de compartir ni genera notificaciones,
     * solo guarda el archivo internamente y notifica el resultado mediante UiEvent.
     */
    fun exportTemplateToJsonFile(context: Context) {
        viewModelScope.launch {
            val template = uiState.value
            val result = templateExporter.exportToPrivateJson(context, template)

            result.onSuccess { file ->
                _eventFlow.emit(UiEvent.ExportLocalSuccess(file))
            }.onFailure {
                _eventFlow.emit(UiEvent.ShowToast(R.string.export_failed))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun exportChecklistAsZipToDownloads(context: Context) {
        viewModelScope.launch {
            val template = uiState.value
            val result = templateExporter.exportChecklistAsZip(context, template)

            result.onSuccess {
                _eventFlow.emit(UiEvent.ShowToast(R.string.export_success_hint))
            }.onFailure {
                _eventFlow.emit(UiEvent.ShowToast(R.string.export_failed))
            }
        }
    }


}