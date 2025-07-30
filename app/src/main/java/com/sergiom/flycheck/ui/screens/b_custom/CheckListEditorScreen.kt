package com.sergiom.flycheck.ui.screens.b_custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiom.flycheck.R
import com.sergiom.flycheck.components.CheckListHeader
import com.sergiom.flycheck.components.CheckListItemCard
import com.sergiom.flycheck.components.CheckListSectionEditor
import com.sergiom.flycheck.viewmodel.TemplateEditorViewModel


@Composable
fun CheckListEditorScreen(
    templateName:String,
    model: String,
    airline: String,
    includeLogo: Boolean,
    sectionCount: Int,
    viewModel: TemplateEditorViewModel = hiltViewModel()
) {
    // Inicializamos el estado del ViewModel solo la primera vez
    LaunchedEffect(Unit) {
        viewModel.initializeTemplate(
            name = templateName,
            model = model,
            airline = airline,
            includeLogo = includeLogo,
            sectionCount = sectionCount
        )
    }

    val template by viewModel.uiState

    Column (
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
    ){

        Row( modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp))
        {
            CheckListHeader(
                name = template.name,
                model = template.aircraftModel,
                airline = template.airline,
                includeLogo = template.includeLogo,
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        template.sections.forEach { section ->
            CheckListSectionEditor(
                sectionId = section.id,
                title = section.title,
                items = section.items,
                onTitleChange = { viewModel.updateSectionTitle(section.id, it) },
                onAddItem = { viewModel.addItemToSection(section.id, it) },
                onToggleItemChecked = { itemId -> viewModel.toggleItemCompleted(section.id, itemId) },
                onItemTitleChange = { itemId, newTitle -> viewModel.updateItemTitle(section.id, itemId, newTitle) },
                onItemActionChange = { itemId, newAction -> viewModel.updateItemAction(section.id, itemId, newAction) }
            )
        }
    }

}
