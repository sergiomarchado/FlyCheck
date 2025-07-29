package com.sergiom.flycheck.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergiom.flycheck.components.CheckListInput
import com.sergiom.flycheck.components.CheckListItemCard
import com.sergiom.flycheck.viewmodel.ChecklistViewModel


@Composable
fun CheckListScreen(
    modifier: Modifier = Modifier,
    viewModel: ChecklistViewModel = viewModel()
) {

    val checkListItems = viewModel.checkListItems

    val text = viewModel.newTextItem

    Column(modifier = modifier.padding(16.dp)) {

        // Composable reutilizable para el campo de texto + botón de agregar
        CheckListInput(
            text = text,
            onTextChanged = { viewModel.onNewTextItemChanged(it) },
            onAddItem = { viewModel.addItem() }
        )

        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn {
            items(checkListItems) { item ->
                CheckListItemCard(
                    item = item,
                    onDelete = { viewModel.deleteItem(item) },
                    onToogleComplete = { viewModel.markAsCompleted(item) }
                )
            }
        }
    }
}
