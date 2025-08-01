package com.sergiom.flycheck.presentation.viewmodel.state

data class CreateCheckListFormState(
    val name: String = "",
    val aircraftModel: String = "",
    val airline: String = "",
    val includeLogo: Boolean = false,
    val sectionCount: Int = 1,
    val nameError: Boolean = false,
    val modelError: Boolean = false

)
