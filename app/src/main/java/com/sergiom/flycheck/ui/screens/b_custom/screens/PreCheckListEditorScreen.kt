package com.sergiom.flycheck.ui.screens.b_custom.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sergiom.flycheck.R
import com.sergiom.flycheck.ui.screens.b_custom.components.editor.FlyCheckTopBar
import com.sergiom.flycheck.presentation.viewmodel.CreatecCustomCheckListViewModel


@Composable
fun PreCheckListEditorScreen(
    // Callback que se invoca cuando el usuario pulsa "Continuar"
    onContinue: (String, String, String, Boolean, Int) -> Unit,

    // Controlador de navegación para manejar los "popBackStack" u otras ruta
    navController: NavHostController
) {
    // ViewModel con Hilt para gestionar estado y lógica de la pantalla
    val viewModel: CreatecCustomCheckListViewModel = hiltViewModel()

    // Escucha y recoge el estado actual de la UI desde el ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Scroll vertical para el contenido de la pantalla
    val scrollState = rememberScrollState()


    Scaffold(

        // TOPBAR O BARRA SUPERIOR CON MENÚ
        topBar = {
            FlyCheckTopBar(
                // 🔙 Vuelve a la pantalla anterior
                onBackClick = {
                    navController.popBackStack()
                },
                onMenuOptionClick = { option ->
                    // TODO: lógica del menú (exportar, ajustes, etc.)
                }
            )
        }
    ) { innerPadding ->

        // CONTENEDOR PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // NOMBRE DE LA CHECKLIST
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_name_label)) },
                isError = uiState.nameError,
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.nameError) {
                Text(
                    text = stringResource(R.string.createchecklistscreen_errortext_name),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // MODELO DE AVIÓN
            OutlinedTextField(
                value = uiState.aircraftModel,
                onValueChange = viewModel::onAircraftModelChanged,
                label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_modelaircraft_label)) },
                isError = uiState.modelError,
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.modelError) {
                Text(
                    text = stringResource(R.string.createchecklistscreen_errortext_aircraftmodel),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // AEROLÍNEA
            OutlinedTextField(
                value = uiState.airline,
                onValueChange = viewModel::onAirlineChanged,
                label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_airlinename_label)) },
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // INTERRUPTOR DE INCLUIR LOGO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.createchecklistscreen_row_switch_includelogo),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = uiState.includeLogo,
                    onCheckedChange = viewModel::onIncludeLogoChanged
                )
            }

            // SELECTOR DE NÚMERO DE SECCIONES INICIALES
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.createchecklistscreen_section_text), modifier = Modifier.weight(1f))

                // 🔼🔽 Controles para aumentar/disminuir el número de secciones
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            viewModel.onSectionCountChange(uiState.sectionCount - 1)
                        }
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.createchecklistscreen_contentDescription_remove)
                        )
                    }

                    Text("${uiState.sectionCount}", style = MaterialTheme.typography.titleMedium)

                    IconButton(
                        onClick = {
                            viewModel.onSectionCountChange(uiState.sectionCount + 1)
                        }
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.createchecklistscreen_contentDescription_add)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CONTINUAR con la creación de la checklist (pasar al editor o plantilla)
            Button(
                onClick = {
                    viewModel.validateAndContinue { form ->
                        onContinue(
                            form.name,
                            form.aircraftModel,
                            form.airline,
                            form.includeLogo,
                            form.sectionCount
                        )
                    }
                },
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 42.dp)
            ) {
                Text(stringResource(R.string.createchecklistscreen_bottom_continue))
            }
        }
    }
}
