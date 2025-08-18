package com.sergiom.flycheck.ui.utils

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

enum class WidthClass { Compact, Medium, Expanded }
enum class HeightClass { Compact, Medium, Expanded }

data class WindowMode(
    val widthDp: Int,
    val heightDp: Int,
    val isLandscape: Boolean,
    val widthClass: WidthClass,
    val heightClass: HeightClass
) {
    val isCompactWidth get() = widthClass == WidthClass.Compact
    val isCompactHeight get() = heightClass == HeightClass.Compact
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun rememberWindowMode(): WindowMode {
    val cfg = LocalConfiguration.current
    val width = cfg.screenWidthDp
    val height = cfg.screenHeightDp
    val isLandscape = cfg.orientation == Configuration.ORIENTATION_LANDSCAPE

    val wClass = when {
        width < 600 -> WidthClass.Compact
        width < 840 -> WidthClass.Medium
        else -> WidthClass.Expanded
    }
    val hClass = when {
        height < 480 -> HeightClass.Compact
        height < 900 -> HeightClass.Medium
        else -> HeightClass.Expanded
    }

    return remember(width, height, isLandscape) {
        WindowMode(width, height, isLandscape, wClass, hClass)
    }
}
