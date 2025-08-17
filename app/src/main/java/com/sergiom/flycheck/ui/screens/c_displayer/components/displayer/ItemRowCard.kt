package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R
import com.sergiom.flycheck.ui.common.ITEM_COMPLETED_COLOR
import com.sergiom.flycheck.ui.common.ITEM_COMPLETED_SCALE
import com.sergiom.flycheck.ui.common.ITEM_DEFAULT_SCALE
import com.sergiom.flycheck.ui.theme.LocalIsDarkTheme
import com.sergiom.flycheck.ui.theme.parseHexPerson

@Composable
internal fun ItemRowCard(
    title: String,
    action: String,
    emphasisHex: String,
    isDone: Boolean,
    hasInfo: Boolean,
    hasImage: Boolean,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier,
    compactBreakpoint: Dp = 420.dp
) {
    val isDark = LocalIsDarkTheme.current
    val baseColor = parseHexPerson(emphasisHex)
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val compact = screenWidthDp < compactBreakpoint

    val bgColor by animateColorAsState(
        targetValue = if (isDone) ITEM_COMPLETED_COLOR else baseColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "bg"
    )
    val scale by animateFloatAsState(
        targetValue = if (isDone) ITEM_COMPLETED_SCALE else ITEM_DEFAULT_SCALE,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val shape = RoundedCornerShape(16.dp)

    // Colores del chip (solo ajustamos el color del texto en dark)
    val chipColors = AssistChipDefaults.assistChipColors(
        labelColor = if (isDark) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(shape)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = bgColor,
            // ← clave: en dark, que TODO el contenido (Text por defecto) sea negro
            contentColor = if (isDark) Color.Black else MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDone) 6.dp else 2.dp),
        shape = shape,
        border = if (isDone) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 0.dp)
        ) {
            if (!compact) {
                // -------- Fila única (ancho cómodo) --------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Medium,
                        softWrap = true,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.weight(1f, fill = true)
                    )

                    if (action.isNotBlank()) {
                        AssistChip(
                            onClick = onClick,
                            label = { Text(action, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            colors = chipColors
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (hasInfo) {
                            IconButton(onClick = onInfoClick) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Información adicional",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (hasImage) {
                            IconButton(onClick = onImageClick) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add_image),
                                    contentDescription = "Item con imagen",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            } else {
                // -------- Layout compacto (2 filas) --------
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Medium,
                    softWrap = true,
                    overflow = TextOverflow.Clip
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (action.isNotBlank()) {
                        AssistChip(
                            onClick = onClick,
                            label = { Text(action, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            colors = chipColors
                        )
                    } else {
                        Spacer(Modifier.width(1.dp))
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (hasInfo) {
                            IconButton(onClick = onInfoClick) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Información adicional",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (hasImage) {
                            IconButton(onClick = onImageClick) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add_image),
                                    contentDescription = "Item con imagen",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
