package com.sergiom.flycheck.ui.screens.b_custom

import com.sergiom.flycheck.ui.screens.b_custom.components.EditorHeader
import com.sergiom.flycheck.ui.screens.b_custom.components.SectionList
import com.sergiom.flycheck.ui.screens.b_custom.components.RenameSectionDialog
import com.sergiom.flycheck.ui.screens.b_custom.components.ConfirmDeleteSectionDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiom.flycheck.ui.events.UiEvent
import com.sergiom.flycheck.viewmodel.TemplateEditorViewModel

@Composable
fun CheckListEditorScreen(
    templateName: String,
    model: String,
    airline: String,
    includeLogo: Boolean,
    sectionCount: Int,
    viewModel: TemplateEditorViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.initializeTemplate(
            name = templateName,
            model = model,
            airline = airline,
            includeLogo = includeLogo,
            sectionCount = sectionCount
        )
    }

    val template by viewModel.uiState.collectAsState()

    var editingSectionId by remember { mutableStateOf<String?>(null) }
    var editingSectionTitle by remember { mutableStateOf("") }

    var deletingSectionId by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            if (event is UiEvent.ShowToast) {
                Toast.makeText(context, context.getString(event.resId), Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        EditorHeader(template)
        Spacer(modifier = Modifier.height(16.dp))
        SectionList(
            template = template,
            onRename = { id, title ->
                editingSectionId = id
                editingSectionTitle = title
            },
            onDelete = { id -> deletingSectionId = id },
            viewModel = viewModel
        )
    }

    if (editingSectionId != null) {
        RenameSectionDialog(
            title = editingSectionTitle,
            onDismiss = { editingSectionId = null },
            onConfirm = {
                val success = viewModel.updateSectionTitle(editingSectionId!!, it)
                if (success) editingSectionId = null
            },
            onTitleChange = { editingSectionTitle = it }
        )
    }

    if (deletingSectionId != null) {
        ConfirmDeleteSectionDialog(
            onConfirm = {
                viewModel.deleteSection(deletingSectionId!!)
                deletingSectionId = null
            },
            onDismiss = { deletingSectionId = null }
        )
    }
}
