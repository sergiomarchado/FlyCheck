package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.viewmodel.player.SectionSummary
import com.sergiom.flycheck.ui.common.ITEM_COMPLETED_COLOR
import com.sergiom.flycheck.ui.theme.LocalIsDarkTheme

@Composable
internal fun SectionTabs(
    summaries: List<SectionSummary>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    if (summaries.isEmpty()) return

    val isDark = LocalIsDarkTheme.current

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(summaries, key = { i, s -> "sec-$i-${s.title}" }) { i, s ->
            val isSelected = i == selectedIndex
            val isComplete = s.total > 0 && s.done >= s.total

            val selectedContainer =
                if (isDark) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.primary
            val selectedLabel =
                if (isDark) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onPrimary

            val colors =
                when {
                    isComplete -> {
                        // completa → verde + texto negro
                        FilterChipDefaults.filterChipColors(
                            containerColor = ITEM_COMPLETED_COLOR,
                            selectedContainerColor = ITEM_COMPLETED_COLOR,
                            labelColor = Color.Black,
                            selectedLabelColor = Color.Black,
                            iconColor = Color.Black,
                            selectedLeadingIconColor = Color.Black,
                            selectedTrailingIconColor = Color.Black
                        )
                    }
                    isSelected -> {
                        // seleccionada (no completa)
                        FilterChipDefaults.filterChipColors(
                            containerColor = selectedContainer,
                            selectedContainerColor = selectedContainer,
                            labelColor = selectedLabel,
                            selectedLabelColor = selectedLabel,
                            iconColor = selectedLabel,
                            selectedLeadingIconColor = selectedLabel,
                            selectedTrailingIconColor = selectedLabel
                        )
                    }
                    else -> {
                        // NO seleccionada y NO completa
                        if (isDark) {
                            // oscuro → gris con texto de alto contraste
                            FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            // claro → **texto blanco** y fondo azul suave
                            FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                labelColor = Color.White,
                                iconColor = Color.White
                            )
                        }
                    }
                }

            val border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null

            FilterChip(
                selected = isSelected,
                onClick = { onSelect(i) },
                label = {
                    Text(
                        text = s.title.ifBlank { "Sección ${i + 1}" },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                shape = MaterialTheme.shapes.small,
                colors = colors,
                border = border
            )
        }
    }
}
