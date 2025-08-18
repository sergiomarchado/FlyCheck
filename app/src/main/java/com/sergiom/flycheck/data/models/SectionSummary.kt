package com.sergiom.flycheck.data.models

import androidx.compose.runtime.Immutable

@Immutable
data class SectionSummary(
    val title: String,
    val done: Int,
    val total: Int
) {
    val clampedDone: Int get() = done.coerceIn(0, total)
    val progress: Float get() = if (total <= 0) 0f else clampedDone.toFloat() / total
}