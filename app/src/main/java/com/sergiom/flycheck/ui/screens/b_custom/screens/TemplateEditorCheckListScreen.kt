package com.sergiom.flycheck.ui.screens.b_custom.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.ExportOption
import com.sergiom.flycheck.data.model.RenameTargetType
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel
import com.sergiom.flycheck.ui.events.UiEvent
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.ExportFab
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.FlatSectionListView
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.FlyCheckTopBar
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.titlesection.ConfirmDeleteSectionDialog
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.utils.ObserveUiEvents
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.utils.RenameDialog

/**
 * Pantalla principal del editor de checklist.
 * Se encarga de mostrar las secciones, subsecciones e ítems que componen una plantilla.
 *
 * @param templateName Nombre de la plantilla a editar.
 * @param model Modelo del avión.
 * @param airline Aerolínea asociada a la plantilla.
 * @param includeLogo Indica si se debe mostrar el logo.
 * @param sectionCount Cantidad de secciones iniciales.
 * @param navController Controlador de navegación.
 * @param viewModel ViewModel asociado a la pantalla.
 */

@SuppressLint("NewApi")
@Composable
fun TemplateEditorCheckListScreen(
    templateName: String,
    model: String,
    airline: String,
    includeLogo: Boolean,
    sectionCount: Int,
    logoUri: Uri?,
    navController: NavHostController,
    viewModel: TemplateEditorViewModel = hiltViewModel()
) {
    // Lógica se ejecuta una vez al entrar en la pantalla: inicializa la plantilla en el ViewModel.
    LaunchedEffect(Unit) {
        viewModel.initializeTemplate(
            name = templateName,
            model = model,
            airline = airline,
            includeLogo = includeLogo,
            sectionCount = sectionCount,
            logoUri = logoUri
        )
    }

    // Observa el estado de la plantilla (secciones, ítems, etc.)
    val template by viewModel.uiState.collectAsState()

    // Estados necesarios para los diálogos de renombrado y eliminación
    var renameTargetId by remember { mutableStateOf<String?>(null) }
    var renameTargetTitle by remember { mutableStateOf("") }
    var renameTargetType by
    remember { mutableStateOf(RenameTargetType.SECTION) }

    var deletingSectionId by remember { mutableStateOf<String?>(null) }

    // Observa eventos de UI (ej. Toasts)
    ObserveUiEvents(viewModel)

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ExportSuccess -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.export_success_local),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is UiEvent.ExportLocalSuccess -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.export_success_local),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is UiEvent.ShowToast -> {
                    Toast.makeText(context, context.getString(event.resId), Toast.LENGTH_SHORT)
                        .show()
                }

                else -> Unit
            }
        }
    }


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
        },
        floatingActionButton = {
            ExportFab { option ->
                when (option) {
                    ExportOption.Local -> {
                        viewModel.exportTemplateToJsonFile(context)
                    }
                    ExportOption.Community -> {
                        // TODO: Implementar lógica para subir al servidor o compartir online
                    }
                    ExportOption.File -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                viewModel.exportChecklistAsZipToDownloads(context)
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.permission_notifications_required),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            viewModel.exportChecklistAsZipToDownloads(context)
                        }
                    }
                }
            }
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Respeta el padding del Scaffold
        ) {

            Spacer(modifier = Modifier.height(16.dp))


            // LISTA DE LA CHECKLIST: secciones, subsecciones e ítems
            FlatSectionListView(
                template = template,
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f),

                // Configura el estado para mostrar el diálogo de renombrado
                onRename = { id, title, type ->
                    renameTargetId = id
                    renameTargetTitle = title
                    renameTargetType = type
                },
                // Configura el estado para mostrar el diálogo de eliminación
                onDelete = { id ->
                    deletingSectionId = id
                }
            )

            // Diálogo: renombrar títulos de sección o subsección
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
                        // Si el cambio fue exitoso, se cierra el diálogo
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
