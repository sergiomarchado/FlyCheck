package com.sergiom.flycheck.ui.screens.b_custom.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sergiom.flycheck.R

@Composable
fun ConfirmDeleteSectionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.checklisteditorscreen_deletesection_title)) },
        text = { Text(stringResource(R.string.checklisteditorscreen_deletesection_warning)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.checklisteditorscreen_deletesection_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.checklisteditorscreen_deletesection_dismiss))
            }
        }
    )
}

