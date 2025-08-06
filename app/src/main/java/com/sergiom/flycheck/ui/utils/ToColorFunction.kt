package com.sergiom.flycheck.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt


fun String.toColor(): Color = try {
    Color(this.toColorInt())
} catch (e:Exception){
    Color.LightGray
}

