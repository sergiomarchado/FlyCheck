package com.sergiom.flycheck.ui.theme


import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

fun parseHexPerson(hex: String?): Color = runCatching {
    if (hex.isNullOrBlank()) Color.Unspecified
    else Color(hex.toColorInt())
}.getOrElse { Color.Unspecified }
