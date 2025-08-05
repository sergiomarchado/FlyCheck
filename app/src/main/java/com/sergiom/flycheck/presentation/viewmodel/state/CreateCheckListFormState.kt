package com.sergiom.flycheck.presentation.viewmodel.state

import android.net.Uri

// Clase de estado que representa el formulario inicial para crear una nueva checklist personalizada.
// Este estado se utiliza en la pantalla "PreCheckListEditorScreen" y se gestiona desde un ViewModel.
data class CreateCheckListFormState(
    val name: String = "",
    val aircraftModel: String = "",
    val airline: String = "",
    val includeLogo: Boolean = false,
    val sectionCount: Int = 1,
    val nameError: Boolean = false,
    val modelError: Boolean = false,
    val logoUri: Uri? = null

)
