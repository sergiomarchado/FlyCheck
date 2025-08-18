package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.models.SectionSummary
import com.sergiom.flycheck.data.models.SubsectionSummary

/**
 * # SectionPickerSheet
 *
 * **Propósito**: BottomSheet que permite al usuario navegar rápidamente entre:
 * - **Secciones** completas de la checklist (con progreso).
 * - **Subsecciones** específicas dentro de la sección actual.
 *
 * **Entradas**:
 * - [summaries]: resumen de cada sección (título, progreso).
 * - [currentIndex]: índice de la sección actual.
 * - [subsections]: lista de subsecciones de la sección actual.
 *
 * **Callbacks**:
 * - [onPickSection]: se invoca al seleccionar una sección completa.
 * - [onPickSubsection]: se invoca al seleccionar directamente una subsección.
 *
 * **UI**:
 * - Muestra una lista de tarjetas (`ElevatedCard`) con barras de progreso.
 * - Se separa visualmente la lista de secciones y subsecciones.
 */
@Composable
internal fun SectionPickerSheet(
    summaries: List<SectionSummary>,
    currentIndex: Int,
    subsections: List<SubsectionSummary>,
    onPickSection: (Int) -> Unit,
    onPickSubsection: (Int) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ---- Título principal ----
        Text(
            text = "Secciones",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // ---- Lista de SECCIONES ----
        summaries.forEachIndexed { index, s ->
            ElevatedCard(
                onClick = { onPickSection(index) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceTint,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {

                    // Si el título está vacío, usamos "Sección N"
                    val title = s.title.ifBlank { "Sección ${index + 1}" }
                    // Marcamos con "• Actual" la sección que coincide con currentIndex
                    Text(
                        title + if (index == currentIndex) " • Actual" else "",
                        fontWeight = FontWeight.SemiBold
                    )
                    // Barra de progreso: proporcional a done / total
                    LinearProgressIndicator(
                        progress = {
                            if (s.total == 0) 0f else s.done.toFloat() / s.total.toFloat()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    // Texto con conteo (ej: "3 / 10")
                    Text("${s.done} / ${s.total}", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // ---- Subsecciones (solo si hay) ----
        if (subsections.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Subsecciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            subsections.forEach { sub ->
                ElevatedCard(
                    onClick = { onPickSubsection(sub.firstGlobalIndex) }, // → salto directo al ítem
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceTint,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {

                        Text(sub.title, fontWeight = FontWeight.Medium)

                        // Barra de progreso de la subsección
                        LinearProgressIndicator(
                            progress = {
                                if (sub.total == 0) 0f else sub.done.toFloat() / sub.total.toFloat()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        // Conteo (ej: "2 / 5")
                        Text("${sub.done} / ${sub.total}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(24.dp))
    }
}
