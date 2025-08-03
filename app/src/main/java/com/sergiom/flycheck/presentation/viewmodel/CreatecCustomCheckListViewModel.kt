package com.sergiom.flycheck.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.sergiom.flycheck.presentation.viewmodel.state.CreateCheckListFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class CreatecCustomCheckListViewModel @Inject constructor() : ViewModel() {

    // Estado interno mutable del formulario
    private val _uiState = MutableStateFlow(CreateCheckListFormState())

    // Estado público e inmutable expuesto a la UI
    val uiState: StateFlow<CreateCheckListFormState> = _uiState.asStateFlow()

    // Actualiza el campo del nombre y su posible error (vacío)
    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, nameError = name.isBlank()) }
    }

    // Actualiza el modelo del avión y su posible error (vacío)
    fun onAircraftModelChanged(model: String) {
        _uiState.update { it.copy(aircraftModel = model, modelError = model.isBlank()) }
    }


    // Actualiza el nombre de la aerolínea (opcional, sin validación)
    fun onAirlineChanged(airline: String) {
        _uiState.update { it.copy(airline = airline) }
    }

    // Cambia el estado del switch "Incluir logo"
    fun onIncludeLogoChanged(value: Boolean) {
        _uiState.update { it.copy(includeLogo = value) }
    }

    // Cambia el número de secciones, limitando entre 1 y 15
    fun onSectionCountChange(newCount: Int) {
        _uiState.update {
            it.copy(sectionCount = newCount.coerceIn(1, 15))
        }
    }

    // Valida los campos obligatorios y si está bien, ejecuta el callback
    fun validateAndContinue(onValid: (CreateCheckListFormState) -> Unit) {
        val current = _uiState.value

        // Comprobaciones de campos obligatorios
        val nameError = current.name.isBlank()
        val modelError = current.aircraftModel.isBlank()

        if (!nameError && !modelError) {
            onValid(current)
        } else {
            _uiState.update {
                it.copy(nameError = nameError, modelError = modelError)
            }
        }
    }

}