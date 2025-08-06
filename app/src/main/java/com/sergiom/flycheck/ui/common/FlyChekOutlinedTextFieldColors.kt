package com.sergiom.flycheck.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun flyCheckOutlinedTextFieldColorsFor(backgroundColor: Color): TextFieldColors {
    val useDarkText = backgroundColor.luminance() > 0.5f
    val textColor = if (useDarkText) Color.Black else Color.White
    val labelColor = if (useDarkText) Color.Black else Color.White
    val borderColor = if (useDarkText) Color.DarkGray else Color.LightGray
    val placeholderColor = if (useDarkText) Color.DarkGray else Color.LightGray

    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = textColor.copy(alpha = 0.6f),
        errorTextColor = MaterialTheme.colorScheme.error,

        focusedContainerColor = backgroundColor,
        unfocusedContainerColor = backgroundColor,
        disabledContainerColor = backgroundColor,
        errorContainerColor = backgroundColor,

        cursorColor = textColor,
        errorCursorColor = MaterialTheme.colorScheme.error,

        focusedBorderColor = borderColor,
        unfocusedBorderColor = borderColor,
        disabledBorderColor = borderColor.copy(alpha = 0.3f),
        errorBorderColor = MaterialTheme.colorScheme.error,

        focusedLabelColor = labelColor,
        unfocusedLabelColor = labelColor.copy(alpha = 0.7f),
        disabledLabelColor = labelColor.copy(alpha = 0.6f),
        errorLabelColor = MaterialTheme.colorScheme.error,

        focusedPlaceholderColor = placeholderColor,
        unfocusedPlaceholderColor = placeholderColor.copy(alpha = 0.7f),
        disabledPlaceholderColor = placeholderColor.copy(alpha = 0.5f),
        errorPlaceholderColor = MaterialTheme.colorScheme.error
    )
}

