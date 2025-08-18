package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.viewmodel.player.SectionSummary
import com.sergiom.flycheck.viewmodel.player.SubsectionSummary

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
        Text(
            text = "Secciones",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        summaries.forEachIndexed { index, s ->
            ElevatedCard(
                onClick = { onPickSection(index) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors( // ← mismo “tinte” que en el editor
                    containerColor = MaterialTheme.colorScheme.surfaceTint,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val title = s.title.ifBlank { "Sección ${index + 1}" }
                    Text(
                        title + if (index == currentIndex) " • Actual" else "",
                        fontWeight = FontWeight.SemiBold
                    )
                    LinearProgressIndicator(
                        progress = {
                            if (s.total == 0) 0f else s.done.toFloat() / s.total.toFloat()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text("${s.done} / ${s.total}", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        if (subsections.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Subsecciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            subsections.forEach { sub ->
                ElevatedCard(
                    onClick = { onPickSubsection(sub.firstGlobalIndex) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceTint,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(sub.title, fontWeight = FontWeight.Medium)
                        LinearProgressIndicator(
                            progress = {
                                if (sub.total == 0) 0f else sub.done.toFloat() / sub.total.toFloat()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text("${sub.done} / ${sub.total}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(24.dp))
    }
}
