package com.sergiom.flycheck.ui.screens.b_custom.components.editor.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sergiom.flycheck.R

/**
 * Diálogo reutilizable para cambiar el nombre de secciones o subsecciones.
 *
 * Muestra un AlertDialog con un campo de texto editable, junto con botones para confirmar o cancelar.
 *
 * @param currentText Texto actual del campo (por ejemplo, el título actual de una sección).
 * @param onTextChange Función que se ejecuta cuando el usuario modifica el texto.
 * @param onDismiss Función que se ejecuta cuando se cierra el diálogo sin confirmar.
 * @param onConfirm Función que se ejecuta al confirmar, pasando el texto introducido.
 * @param dialogTitle Título del diálogo (por defecto usa el string del título de renombrar).
 */
@Composable
fun RenameDialog(
    currentText: String,              // Texto actual del campo a renombrar.
    onTextChange: (String) -> Unit,   // Callback: actualiza el texto mientras el usuario escribe.
    onDismiss: () -> Unit,            // Callback cuando se cancela o cierra el diálogo.
    onConfirm: (String) -> Unit,      // Callback al pulsar "Aceptar", pasa el texto actualizado.
    // Título por defecto
    dialogTitle: String = stringResource(R.string.checklisteditorscreen_dialog_title)
) {

    AlertDialog(
        onDismissRequest = onDismiss, // También se llama al tocar fuera del diálogo o pulsar atrás.
        // Título del diálogo
        title = { Text(dialogTitle) },

        // Cuerpo del Diálogo con campo de texto
        text = {
            OutlinedTextField(
                value = currentText,
                onValueChange = onTextChange,
                singleLine = true,
                label = { Text(stringResource(R.string.checklisteditorscreen_dialog_outlined_newtitle)) }
            )
        },
        // Botón de confirmación
        confirmButton = {
            TextButton(onClick = { onConfirm(currentText) }) {
                Text(stringResource(R.string.checklisteditorscreen_dialog_confirmbutton))
            }
        },

        // Botón de cancelar
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.checklisteditorscreen_dialog_dismissbutton))
            }
        }
    )
}
