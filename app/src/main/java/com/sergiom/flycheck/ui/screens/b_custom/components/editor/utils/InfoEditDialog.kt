package com.sergiom.flycheck.ui.screens.b_custom.components.editor.utils

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R

/**
 * Diálogo utilizado para añadir o editar información adicional en un ítem de la checklist.
 *
 * @param currentTitle Valor inicial del campo título.
 * @param currentBody Valor inicial del campo cuerpo de la información.
 * @param onDismiss Callback cuando se cierra el diálogo sin guardar.
 * @param onConfirm Callback que se invoca con los nuevos valores al pulsar "Guardar".
 */
@Composable
fun InfoEditDialog(
    currentTitle: String,
    currentBody: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    // Estados locales para el contenido del diálogo
    var title by remember { mutableStateOf(currentTitle) }
    var body by remember { mutableStateOf(currentBody) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.infoeditdialog_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.infoeditdialog_body))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.infoeditdialog_title_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text(stringResource(R.string.infoeditdialog_body_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, body) }) {
                Text(stringResource(R.string.checklisteditorscreen_dialog_confirmbutton))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.checklisteditorscreen_dialog_dismissbutton))
            }
        }
    )
}
