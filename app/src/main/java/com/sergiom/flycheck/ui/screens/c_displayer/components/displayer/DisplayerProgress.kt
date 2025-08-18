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
/**
 * ## DisplayerProgress
 *
 * **Propósito**: Mostrar el progreso de la sección actual de la checklist.
 *
 * Renderiza:
 * - Una barra de progreso lineal proporcional a ítems completados / total.
 * - Un texto auxiliar con el conteo (`done/total`) y el título de la sección.
 *
 * @param sectionDone Número de ítems completados en la sección.
 * @param sectionTotal Número total de ítems de la sección.
 * @param sectionTitle Título descriptivo de la sección actual.
 */
@Composable
internal fun DisplayerProgress(
    sectionDone: Int,
    sectionTotal: Int,
    sectionTitle: String
) {
    if (sectionTotal <= 0) return  // evita división por cero y render innecesario
    val p = sectionDone.toFloat() / sectionTotal.toFloat()
    // Barra de progreso lineal
    LinearProgressIndicator(progress = { p }, modifier = Modifier.fillMaxWidth())

    Spacer(modifier = Modifier.height(8.dp))

    // Etiqueta con conteo y título
    Text("$sectionDone/$sectionTotal • $sectionTitle", style = MaterialTheme.typography.labelLarge)
}
