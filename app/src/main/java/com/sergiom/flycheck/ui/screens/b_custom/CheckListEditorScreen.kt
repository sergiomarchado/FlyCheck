package com.sergiom.flycheck.ui.screens.b_custom

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.section.ConfirmDeleteSectionDialog
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.header.EditorHeaderMain
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.FlyCheckTopBar
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.ObserveUiEvents
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.section.RenameSectionDialog
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.SectionList
import com.sergiom.flycheck.viewmodel.TemplateEditorViewModel

@Composable
fun CheckListEditorScreen(
    templateName: String,
    model: String,
    airline: String,
    includeLogo: Boolean,
    sectionCount: Int,
    navController: NavHostController,
    viewModel: TemplateEditorViewModel = hiltViewModel()
) {
    // Inicializar plantilla
    LaunchedEffect(Unit) {
        viewModel.initializeTemplate(
            name = templateName,
            model = model,
            airline = airline,
            includeLogo = includeLogo,
            sectionCount = sectionCount
        )
    }

    // Estado actual de la plantilla
    val template by viewModel.uiState.collectAsState()

    // Estados para los diálogos
    var editingSectionId by remember { mutableStateOf<String?>(null) }
    var editingSectionTitle by remember { mutableStateOf("") }
    var deletingSectionId by remember { mutableStateOf<String?>(null) }

    // Mostrar toasts u otros eventos
    ObserveUiEvents(viewModel)

    Scaffold(
        topBar = {
            FlyCheckTopBar(
                onBackClick = {
                    navController.popBackStack()
                },
                onMenuOptionClick = { option ->
                    // TODO: lógica del menú (exportar, ajustes, etc.)
                }
            )
        }
    ) { innerPadding ->

        SectionList(
            template = template,
            header = {
                Spacer(modifier = Modifier.height(16.dp))
                EditorHeaderMain(template)
            },
            onRename = { id, title ->
                editingSectionId = id
                editingSectionTitle = title
            },
            onDelete = { id -> deletingSectionId = id },
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )

        // Diálogo: renombrar sección
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

        // Diálogo: eliminar sección
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
}
