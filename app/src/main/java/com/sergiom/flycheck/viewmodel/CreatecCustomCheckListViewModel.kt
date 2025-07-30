package com.sergiom.flycheck.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sergiom.flycheck.data.model.CheckListConfig
import com.sergiom.flycheck.data.model.CheckListSection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CreateCheckListViewModel @Inject constructor() : ViewModel() {
    // Estado observable: contiene todos los datos que el usuario está configurando
    var config by mutableStateOf(CheckListConfig())
        private set

    // Contador de IDs únicos para secciones
    private var nextSectionId = 0

    // Actualiza el nombre de la checklist
    fun onNameChanged(name: String){
        config = config.copy(name = name)
    }

    // Actualiza el modelo del avión
    fun onAircraftModelChanged(model: String){
        config = config.copy(modelAircraft = model)
    }

    // Actualiza el nombre de la aerolínea (opcional)
    fun onAirlineChanged(airline: String){
        config = config.copy(airline = airline)
    }

    // Cambia si se incluirá el logo o no
    fun onIncludeLogoChanged(value: Boolean){
        config = config.copy(includeLogo = value)
    }

    // Inicializa las secciones con títulos por defecto y un ID único para cada una
    fun initializeSections(count: Int){
        val newSections = MutableList(count.coerceIn(1,10)) { _ ->
            CheckListSection(
                id = nextSectionId++,
                title = "Section $nextSectionId")
        }
        config = config.copy(sections = newSections.toMutableList())
    }

    fun updateSectionTitle(sectionId: Int, newTitle: String){
        val updatedSections = config.sections.map { section ->
            if(section.id == sectionId) section.copy(title = newTitle) else section
        }
        config = config.copy(sections = updatedSections.toMutableList())
    }

}