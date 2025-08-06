package com.sergiom.flycheck.ui.screens.b_editor.components.editor.item.subcomponents

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import com.sergiom.flycheck.ui.common.flyCheckOutlinedTextFieldColorsFor


@Composable
fun ItemTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit,
    label: String,
    modifier: Modifier,
    backgroundColor: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.onFocusChanged { focus ->
            if (!focus.isFocused) onFocusLost()
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = false,
        minLines = 1,
        maxLines = 4,
        colors = flyCheckOutlinedTextFieldColorsFor(backgroundColor)
    )
}

