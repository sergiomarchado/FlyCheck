package com.sergiom.flycheck.ui.screens.b_editor.components.editor.item.subcomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R

@Composable
fun MoveButtons(onMoveUp: () -> Unit, onMoveDown: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        IconButton(onClick = onMoveUp) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(R.string.checklistitemcard_button_move_up),
                tint = Color.DarkGray
            )
        }
        IconButton(onClick = onMoveDown) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.checklistitemcard_button_move_down),
                tint = Color.DarkGray
            )
        }
    }
}


