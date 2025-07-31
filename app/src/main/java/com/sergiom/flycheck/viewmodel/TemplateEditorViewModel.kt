package com.sergiom.flycheck.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sergiom.flycheck.data.model.CheckListItem
import com.sergiom.flycheck.data.model.CheckListSection
import com.sergiom.flycheck.data.model.CheckListTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TemplateEditorViewModel @Inject constructor(): ViewModel() {

    private val _uiState = mutableStateOf(CheckListTemplate())

    val uiState = _uiState

    fun setTemplateName(name: String){
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun setAircraftModel(model: String) {
        _uiState.value = _uiState.value.copy(aircraftModel = model)
    }

    fun setAirline(name: String) {
        _uiState.value = _uiState.value.copy(airline = name)
    }

    fun toggleIncludeLogo() {
        _uiState.value = _uiState.value.copy(includeLogo = !_uiState.value.includeLogo)
    }

    fun addSection(title: String){
        val newSection = CheckListSection(title = title)
        _uiState.value = _uiState.value.copy(
            sections = _uiState.value.sections + newSection
        )
    }

    fun deleteSection (sectionId: String){
        _uiState.value = _uiState.value.copy(
            sections = _uiState.value.sections.filterNot { it.id == sectionId }
        )
    }

    fun addItemToSection(sectionId: String, title: String, action: String = "", colorHex: String = "#ECECEC") {
        _uiState.value = _uiState.value.copy(
            sections = _uiState.value.sections.map { section ->
                if (section.id == sectionId) {
                    section.copy(
                        items = section.items + CheckListItem(
                            title = title,
                            action = action,
                            backgroundColorHex = colorHex
                        )
                    )
                } else section
            }
        )
    }

    fun toggleItemCompleted(sectionId: String, itemId: String){
        _uiState.value = _uiState.value.copy(
            sections = _uiState.value.sections.map { section ->
                if(section.id == sectionId){
                    section.copy(
                        items = section.items.map { item ->
                            if(item.id == itemId) item.copy(completed = !item.completed)
                            else item
                        }
                    )
                }else section
            }
        )
    }

    fun updateItemTitle(sectionId: String, itemId: String, newTitle: String){
        _uiState.value = _uiState.value.copy(
            sections = _uiState.value.sections.map { section ->
                if(section.id == sectionId){
                    section.copy(
                        items = section.items.map { item->
                            if(item.id == itemId) item.copy(title = newTitle)
                            else item
                        }
                    )
                }else section
            }
        )
    }

    fun updateItemColor(sectionId: String, itemId: String, newColorHex: String){
        _uiState.value = _uiState.value.copy(
            sections = _uiState.value.sections.map { section ->
                if(section.id == sectionId){
                    section.copy(
                        items = section.items.map { item ->
                            if(item.id == itemId) item.copy(backgroundColorHex = newColorHex)
                            else item
                        }
                    )
                } else section
            }
        )
    }

    fun initializeTemplate(
        name: String,
        model: String,
        airline: String,
        includeLogo: Boolean,
        sectionCount: Int
    ){
        // Solo inicializa si no hay secciones (evita re-inicializar si se recomponen)
        if (_uiState.value.sections.isNotEmpty()) return

        val sections = List(sectionCount) { index ->
            CheckListSection(title = "Section ${index + 1}")
        }

        _uiState.value = CheckListTemplate(
            name = name,
            aircraftModel = model,
            airline = airline,
            includeLogo = includeLogo,
            sections = sections
        )
    }

    fun updateItemAction(sectionId: String, itemId: String, newAction: String) {
        _uiState.value = _uiState.value.copy(
            sections = _uiState.value.sections.map { section ->
                if (section.id == sectionId) {
                    section.copy(
                        items = section.items.map { item ->
                            if (item.id == itemId) item.copy(action = newAction)
                            else item
                        }
                    )
                } else section
            }
        )
    }

    fun updateSectionTitle(sectionId: String, newTitle: String) {
        _uiState.value = _uiState.value.copy(
            sections = _uiState.value.sections.map { section ->
                if (section.id == sectionId) section.copy(title = newTitle)
                else section
            }
        )
    }


}