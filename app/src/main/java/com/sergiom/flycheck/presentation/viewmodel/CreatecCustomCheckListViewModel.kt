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

    private val _uiState = MutableStateFlow(CreateCheckListFormState())
    val uiState: StateFlow<CreateCheckListFormState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, nameError = name.isBlank()) }
    }

    fun onAircraftModelChanged(model: String) {
        _uiState.update { it.copy(aircraftModel = model, modelError = model.isBlank()) }
    }

    fun onAirlineChanged(airline: String) {
        _uiState.update { it.copy(airline = airline) }
    }

    fun onIncludeLogoChanged(value: Boolean) {
        _uiState.update { it.copy(includeLogo = value) }
    }

    fun onSectionCountChange(newCount: Int) {
        _uiState.update {
            it.copy(sectionCount = newCount.coerceIn(1, 15))
        }
    }

    fun validateAndContinue(onValid: (CreateCheckListFormState) -> Unit) {
        val current = _uiState.value
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