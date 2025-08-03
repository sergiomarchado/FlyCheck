package com.sergiom.flycheck.ui.screens.b_custom.components.editor.titlesection

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sergiom.flycheck.R

/**
 * Diálogo de confirmación para eliminar una sección.
 * Este componente muestra un `AlertDialog` que le pide al usuario que confirme
 * si realmente desea eliminar una sección de la checklist.
 *
 * @param onConfirm Función que se ejecuta cuando el usuario confirma la eliminación.
 * @param onDismiss Función que se ejecuta cuando el usuario cierra o cancela el diálogo.
 */
@Composable
fun ConfirmDeleteSectionDialog(
    onConfirm: () -> Unit,  // Callback al pulsar el botón "Eliminar"
    onDismiss: () -> Unit   // Callback al pulsar el botón "Cancelar" o fuera del diálogo
) {
    AlertDialog(
        // Se ejecuta cuando el usuario toca fuera del diálogo o pulsa atrás
        onDismissRequest = onDismiss,

        // Título del diálogo
        title = { Text(stringResource(R.string.checklisteditorscreen_deletesection_title)) },

        // Cuerpo del mensaje, normalmente una advertencia al usuario
        text = { Text(stringResource(R.string.checklisteditorscreen_deletesection_warning)) },

        // Botón de confirmación
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.checklisteditorscreen_deletesection_confirm))
            }
        },
        // Botón de cancelar
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.checklisteditorscreen_deletesection_dismiss))
            }
        }
    )
}

