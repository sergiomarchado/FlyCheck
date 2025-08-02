package com.sergiom.flycheck.ui.screens.b_custom.components.editor.section

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sergiom.flycheck.R

@Composable
fun RenameDialog(
    currentText: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    dialogTitle: String = stringResource(R.string.checklisteditorscreen_dialog_title)
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            OutlinedTextField(
                value = currentText,
                onValueChange = onTextChange,
                singleLine = true,
                label = { Text(stringResource(R.string.checklisteditorscreen_dialog_outlined_newtitle)) }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(currentText) }) {
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
