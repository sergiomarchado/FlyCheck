package com.sergiom.flycheck.ui.screens.b_custom.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sergiom.flycheck.R

@Composable
fun RenameSectionDialog(
    title: String,
    onTitleChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.checklisteditorscreen_dialog_title)) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                singleLine = true,
                label = { Text(stringResource(R.string.checklisteditorscreen_dialog_outlined_newtitle)) }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title) }) {
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

