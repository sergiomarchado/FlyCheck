package com.sergiom.flycheck.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sergiom.flycheck.data.model.CheckListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor() : ViewModel() {

    private val _checkListItems = mutableStateListOf<CheckListItem>()
    val checkListItems: List<CheckListItem> = _checkListItems

    private var nextId = 0

    var newTextItem by mutableStateOf("")
        private set

    fun onNewTextItemChanged(newText: String) {
        newTextItem = newText
    }

    fun addItem() {
        if (newTextItem.isNotBlank()) {
            _checkListItems.add(
                CheckListItem(
                    title = newTextItem,
                )
            )
            newTextItem = ""
        }
    }

    fun deleteItem(item: CheckListItem) {
        _checkListItems.remove(item)
    }

    fun markAsCompleted(item: CheckListItem) {
        val index = _checkListItems.indexOf(item)
        if (index != -1) {
            _checkListItems[index] = item.copy(completed = !item.completed)
        }
    }
}
