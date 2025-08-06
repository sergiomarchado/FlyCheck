package com.sergiom.flycheck.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sergiom.flycheck.data.models.CheckListItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor() : ViewModel() {

    private val _checkListItemModels = mutableStateListOf<CheckListItemModel>()
    val checkListItemModels: List<CheckListItemModel> = _checkListItemModels

    private var nextId = 0

    var newTextItem by mutableStateOf("")
        private set

    fun onNewTextItemChanged(newText: String) {
        newTextItem = newText
    }

    fun addItem() {
        if (newTextItem.isNotBlank()) {
            _checkListItemModels.add(
                CheckListItemModel(
                    title = newTextItem,
                )
            )
            newTextItem = ""
        }
    }

    fun deleteItem(item: CheckListItemModel) {
        _checkListItemModels.remove(item)
    }

    fun markAsCompleted(item: CheckListItemModel) {
        val index = _checkListItemModels.indexOf(item)
        if (index != -1) {
            _checkListItemModels[index] = item.copy(completed = !item.completed)
        }
    }
}
