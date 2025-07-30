package com.sergiom.flycheck.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiom.flycheck.R
import com.sergiom.flycheck.viewmodel.CreateCheckListViewModel


@Composable
fun CreateCustomCheckListScreen(
    onContinue: (String) -> Unit // Pasar config completa en el futuro

) {
    val viewModel: CreateCheckListViewModel = hiltViewModel()
    val config = viewModel.config

    var sectionCount by rememberSaveable { mutableIntStateOf(1) }

    var nameError by rememberSaveable { mutableStateOf(false) }
    var modelError by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.createchecklistscreen_text_title),
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = config.name,
            onValueChange = {
                viewModel.onNameChanged(it)
                nameError = it.isBlank()
            },
            label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_name_label)) },
            isError = nameError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if(nameError){
            Text(
                text = "Check List name is required",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = config.modelAircraft,
            onValueChange = {
                viewModel.onAircraftModelChanged(it)
                modelError = it.isBlank()
            },
            label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_modelaircraft_label)) },
            isError = modelError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (modelError){
            Text(
                text = "Aircraft model is required",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = config.airline,
            onValueChange = viewModel::onAirlineChanged,
            label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_airlinename_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.createchecklistscreen_row_switch_includelogo),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = config.includeLogo,
                onCheckedChange = viewModel::onIncludeLogoChanged
            )
        }

        // Selector de número de secciones con botones
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.createchecklistscreen_section_text), modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = {
                        sectionCount = (sectionCount - 1).coerceAtLeast(1)
                    }
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.createchecklistscreen_contentDescription_remove)
                    )
                }

                Text("$sectionCount", style = MaterialTheme.typography.bodyLarge)

                IconButton(
                    onClick = {
                        sectionCount = (sectionCount + 1).coerceAtMost(15)
                    }
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.createchecklistscreen_contentDescription_add)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                // Validación antes de Continuar
                nameError = config.name.isBlank()
                modelError = config.modelAircraft.isBlank()

                if (!nameError && !modelError){
                    viewModel.initializeSections(sectionCount)
                    onContinue(config.name)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.createchecklistscreen_bottom_continue))
        }
    }
}