package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun DisplayerProgress(
    sectionDone: Int,
    sectionTotal: Int,
    sectionTitle: String
) {
    if (sectionTotal <= 0) return
    val p = sectionDone.toFloat() / sectionTotal.toFloat()
    LinearProgressIndicator(progress = { p }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    Text("$sectionDone/$sectionTotal • $sectionTitle", style = MaterialTheme.typography.labelLarge)
}
