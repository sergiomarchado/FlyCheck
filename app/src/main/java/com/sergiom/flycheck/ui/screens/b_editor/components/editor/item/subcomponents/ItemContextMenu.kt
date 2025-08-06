package com.sergiom.flycheck.ui.screens.b_editor.components.editor.item.subcomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sergiom.flycheck.R

@Composable
fun ItemContextMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onMoveToggle: () -> Unit,
    onDelete: () -> Unit,
    onAddInfo: () -> Unit,
    onAddImage: () -> Unit,
    onToggleImportant: () -> Unit,
    isImportant: Boolean,
    onMenuOpen: () -> Unit
) {
    Box {
        IconButton(onClick = onMenuOpen) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.checklistitemcard_icon_moreopcions),
                tint = Color.DarkGray
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.checklistitemcard_context_menu_move)) },
                onClick = onMoveToggle
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.checklistitemcard_menu_deleteitem)) },
                onClick = onDelete
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.checklistitemcard_context_menu_add_info)) },
                onClick = onAddInfo
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.checklistitemcard_context_menu_add_image)) },
                onClick = onAddImage
            )
            DropdownMenuItem(
                text = {
                    Text(
                        if (isImportant)
                            stringResource(R.string.checklistitemcard_context_menu_unmark_important)
                        else
                            stringResource(R.string.checklistitemcard_context_menu_mark_important)
                    )
                },
                onClick = onToggleImportant
            )
        }
    }
}
