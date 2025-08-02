package com.sergiom.flycheck.ui.screens.b_custom

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sergiom.flycheck.R
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.FlatSectionListView
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.FlyCheckTopBar
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.ObserveUiEvents
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.header.EditorHeaderMain
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.section.ConfirmDeleteSectionDialog
import com.sergiom.flycheck.data.model.RenameTargetType
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.section.RenameDialog


@Composable
fun TemplateEditorCheckListScreen(
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
    var renameTargetId by remember { mutableStateOf<String?>(null) }
    var renameTargetTitle by remember { mutableStateOf("") }
    var renameTargetType by
    remember { mutableStateOf(RenameTargetType.SECTION) }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            EditorHeaderMain(template)

            FlatSectionListView(
                template = template,
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
                    .padding(innerPadding),
                onRename = { id, title, type ->
                    renameTargetId = id
                    renameTargetTitle = title
                    renameTargetType = type
                },
                onDelete = { id ->
                    deletingSectionId = id
                }
            )

            // Diálogo: renombrar títulos sección o subsección
            if (renameTargetId != null) {
                RenameDialog(
                    currentText = renameTargetTitle,
                    onTextChange = { renameTargetTitle = it },
                    onDismiss = { renameTargetId = null },
                    onConfirm = { newTitle ->
                        val success = when (renameTargetType) {
                            RenameTargetType.SECTION -> viewModel.updateSectionTitle(
                                renameTargetId!!,
                                newTitle
                            )

                            RenameTargetType.SUBSECTION -> viewModel.updateSubsectionTitle(
                                renameTargetId!!,
                                newTitle
                            )
                        }
                        if (success) renameTargetId = null
                    },
                    dialogTitle = when (renameTargetType) {
                        RenameTargetType.SECTION -> stringResource(R.string.checklisteditorscreen_dialog_title_section)
                        RenameTargetType.SUBSECTION -> stringResource(R.string.checklisteditorscreen_dialog_title_subsection)
                    }
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
}
