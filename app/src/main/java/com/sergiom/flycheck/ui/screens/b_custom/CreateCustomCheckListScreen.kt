package com.sergiom.flycheck.ui.screens.b_custom

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiom.flycheck.R
import com.sergiom.flycheck.viewmodel.CreatecCustomCheckListViewModel


@Composable
fun CreateCustomCheckListScreen(
    onContinue: (String, String, String, Boolean, Int) -> Unit
) {
    val viewModel: CreatecCustomCheckListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.createchecklistscreen_text_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Check List Name
        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChanged,
            label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_name_label)) },
            isError = uiState.nameError,
            shape = MaterialTheme.shapes.extraLarge,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if(uiState.nameError){
            Text(
                text = stringResource(R.string.createchecklistscreen_errortext_name),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Aircraft Model
        OutlinedTextField(
            value = uiState.aircraftModel,
            onValueChange = viewModel::onAircraftModelChanged,
            label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_modelaircraft_label)) },
            isError = uiState.modelError,
            shape = MaterialTheme.shapes.extraLarge,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.modelError){
            Text(
                text = stringResource(R.string.createchecklistscreen_errortext_aircraftmodel),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Airline (optional)
        OutlinedTextField(
            value = uiState.airline,
            onValueChange = viewModel::onAirlineChanged,
            label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_airlinename_label)) },
            shape = MaterialTheme.shapes.extraLarge,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Include Logo
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

        // Selector section number
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.createchecklistscreen_section_text), modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        viewModel.onSectionCountChange(uiState.sectionCount -1)
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

        Spacer(modifier = Modifier.weight(1f))

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
            modifier = Modifier.fillMaxWidth().padding(bottom = 42.dp)
        ) {
            Text(stringResource(R.string.createchecklistscreen_bottom_continue))
        }
    }
}