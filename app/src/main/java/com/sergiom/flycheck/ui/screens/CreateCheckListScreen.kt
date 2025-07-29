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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergiom.flycheck.R
import com.sergiom.flycheck.viewmodel.CreateCheckListViewModel


@Composable
fun CreateCheckListScreen(
    viewModel: CreateCheckListViewModel = viewModel(),
    onContinue: (String) -> Unit // Pasar config completa en el futuro

) {
    val config = viewModel.config

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
            onValueChange = viewModel::onNameChanged,
            label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_name_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = config.modelAircraft,
            onValueChange = viewModel::onAircraftModelChanged,
            label = { Text(stringResource(R.string.createchecklistscreen_outlinedtextfield_modelaircraft_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

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
            Text("Secciones", modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = {
                        val new = (config.sectionsNumber - 1).coerceAtLeast(1)
                        viewModel.onSectionNumberChanged(new)
                    }
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.createchecklistscreen_contentDescription_remove)
                    )
                }

                Text("${config.sectionsNumber}", style = MaterialTheme.typography.bodyLarge)

                IconButton(
                    onClick = {
                        val new = (config.sectionsNumber + 1).coerceAtMost(10)
                        viewModel.onSectionNumberChanged(new)
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
            onClick = { onContinue(config.name) }, // En el futuro puedes pasar el objeto completo
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.createchecklistscreen_bottom_continue))
        }
    }
}