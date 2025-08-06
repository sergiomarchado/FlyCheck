package com.sergiom.flycheck.ui.screens.b_editor.components.editor

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.models.ExportOption
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.utils.ExportDialogOption
import com.sergiom.flycheck.ui.common.FAB_DEFAULT_BACKGROUND_COLOR_DARK


@Composable
fun ExportFab(onExportOptionSelected: (ExportOption) -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    // Detecta si el sistema está en modo oscuro para ajustar el color del FAB.
    val isDark = isSystemInDarkTheme()
    val defaultTopBarColor =
        if(isDark) FAB_DEFAULT_BACKGROUND_COLOR_DARK else MaterialTheme.colorScheme.primary

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.exportdialog_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.exportdialog_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ExportDialogOption(
                        title = stringResource(R.string.exportdialog_option_local),
                        description = stringResource(R.string.export_fab_option_local),
                        onClick = {
                            showDialog = false
                            onExportOptionSelected(ExportOption.Local)
                        }
                    )

                    ExportDialogOption(
                        title = stringResource(R.string.exportdialog_option_community),
                        description = stringResource(R.string.export_fab_option_community),
                        onClick = {
                            showDialog = false
                            onExportOptionSelected(ExportOption.Community)
                        }
                    )

                    ExportDialogOption(
                        title = stringResource(R.string.exportdialog_option_file),
                        description = stringResource(R.string.export_fab_option_file),
                        onClick = {
                            showDialog = false
                            onExportOptionSelected(ExportOption.File)
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.exportdialog_cancel))
                }
            }
        )
    }

    FloatingActionButton(
        onClick = { showDialog = true },
        containerColor = defaultTopBarColor

    ) {
        Icon(
            painter = painterResource(R.drawable.ic_fab_export),
            contentDescription = stringResource(R.string.export_fab_contentdesc))
    }
}

