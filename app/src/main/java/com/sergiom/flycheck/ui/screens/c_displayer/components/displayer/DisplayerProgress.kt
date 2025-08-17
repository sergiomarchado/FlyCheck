package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun DisplayerProgress(
    sectionDone: Int,
    sectionTotal: Int,
    sectionTitle: String
) {
    if (sectionTotal <= 0) return
    val p = sectionDone.toFloat() / sectionTotal.toFloat()
    LinearProgressIndicator(progress = { p }, modifier = Modifier.fillMaxWidth())
    Text("$sectionDone/$sectionTotal • $sectionTitle", style = MaterialTheme.typography.labelMedium)
}
