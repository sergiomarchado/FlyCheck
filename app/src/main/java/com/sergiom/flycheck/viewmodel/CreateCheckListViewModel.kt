package com.sergiom.flycheck.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sergiom.flycheck.data.model.CheckListConfig


class CreateCheckListViewModel : ViewModel() {
    var config by mutableStateOf(CheckListConfig())
        private set

    fun onNameChanged(name: String){
        config = config.copy(name = name)
    }

    fun onAircraftModelChanged(model: String){
        config = config.copy(modelAircraft = model)
    }

    fun onAirlineChanged(airline: String){
        config = config.copy(airline = airline)
    }

    fun onIncludeLogoChanged(value: Boolean){
        config = config.copy(includeLogo = value)
    }

    fun onSectionNumberChanged(n: Int){
        config = config.copy(sectionsNumber = n.coerceIn(1,10)) // MÁX 10
    }
}