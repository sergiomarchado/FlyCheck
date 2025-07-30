package com.sergiom.flycheck.util

import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor


fun String.toColor(): Color = try {
    Color(AndroidColor.parseColor(this))
} catch (e:Exception){
    Color.LightGray
}

