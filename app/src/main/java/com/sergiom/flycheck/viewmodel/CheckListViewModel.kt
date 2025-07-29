package com.sergiom.flycheck.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sergiom.flycheck.data.model.CheckListItem

class ChecklistViewModel : ViewModel() {

    private val _checkListItems = mutableStateListOf<CheckListItem>()

    val checkListItems: List<CheckListItem> = _checkListItems

    private var nextId = 0


    var newTextItem by mutableStateOf("")
        private set

    /*
    * Función para actualizar el texto cuando el usuario escribe en el TextField.
    * Solo se puede modificar desde aquí (por eso el setter está en private).
    */
    fun onNewTextItemChanged(nuevoTexto: String) {
        newTextItem = nuevoTexto
    }

    /*
    * Función que agrega un nuevo ítem a la checklist.
    * Solo se agrega si el texto no está en blanco.
    * Después de agregarlo, limpiamos el campo de texto.
    */
    fun addItem() {
        if (newTextItem.isNotBlank()) {
            _checkListItems.add(CheckListItem(id = nextId++, text = newTextItem))
            newTextItem = "" // Limpiamos el campo después de agregar
        }
    }

    /*
    * Función que elimina un ítem de la checklist.
    * Al eliminarlo, Compose detecta automáticamente el cambio gracias a la lista observable.
    */
    fun deleteItem(item: CheckListItem) {
        _checkListItems.remove(item)
        /*
        * Al ser una data class, Kotlin compara por contenido (equals) y no por referencia.
        * Más adelante podríamos usar el id para mayor seguridad.
        */
    }

    /*
    * Esta función cambia el estado de completado de un ítem.
    * Si estaba en false (no completado), pasa a true (completado), y viceversa.
    */
    fun markAsCompleted(item: CheckListItem) {

        val index = _checkListItems.indexOf(item)

        if (index != -1) {
            _checkListItems[index] = item.copy(completed = !item.completed)
            /*
            * copy() crea una nueva copia del objeto con el valor "completada" invertido.
            * Al asignarla a la misma posición en la lista, Compose detecta el cambio y recompone.
            */
        }
    }
}
