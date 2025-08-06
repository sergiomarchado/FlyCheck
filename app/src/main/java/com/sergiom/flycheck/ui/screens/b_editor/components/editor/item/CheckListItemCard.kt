package com.sergiom.flycheck.ui.screens.b_editor.components.editor.item

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.models.CheckListItemModel
import com.sergiom.flycheck.ui.common.ITEM_COMPLETED_COLOR
import com.sergiom.flycheck.ui.common.ITEM_COMPLETED_SCALE
import com.sergiom.flycheck.ui.common.ITEM_DEFAULT_BACKGROUND_COLOR_DARK
import com.sergiom.flycheck.ui.common.ITEM_DEFAULT_BACKGROUND_COLOR_LIGHT
import com.sergiom.flycheck.ui.common.ITEM_DEFAULT_SCALE
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.item.subcomponents.ItemContextMenu
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.item.subcomponents.ItemSideIcons
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.item.subcomponents.ItemTextField
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.item.subcomponents.MoveButtons

// REPRESENTA UNA TARJETA INDIVIDUAL DE CADA UNO DE LOS ITEMS DENTRO DE LA CHECKLIST
@Composable
fun CheckListItemCard(
    item: CheckListItemModel,
    onToggleChecked: () -> Unit,
    onTitleChange: (String) -> Unit,
    onActionChange: (String) -> Unit,
    onDeleteItem: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onAddInfoClick: () -> Unit,
    onViewInfoClick: () -> Unit,
    onAddImageClick: () -> Unit,
    onViewImageClick: () -> Unit,
    onToggleImportant: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determinar si el tema actual es oscuro para ajustar el color de fondo por defecto
    val isDark = isSystemInDarkTheme()
    val defaultBackgroundColor =
        if (isDark) ITEM_DEFAULT_BACKGROUND_COLOR_DARK else ITEM_DEFAULT_BACKGROUND_COLOR_LIGHT

    // Determinar el color de fondo final del ítem en función de si está completado
    val backgroundColor = if (item.completed) {
        ITEM_COMPLETED_COLOR
    } else {
        runCatching { Color(item.backgroundColorHex.toColorInt()) }
            .getOrElse { defaultBackgroundColor }
    }

    // Aplicar una animación suave de escala al marcar el ítem como completado
    val scale by animateFloatAsState(
        targetValue = if (item.completed) ITEM_COMPLETED_SCALE else ITEM_DEFAULT_SCALE,
        label = "scale"
    )

    // Feedback háptico (vibración corta) al marcar como completado
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(item.completed) {
        if (item.completed) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Estado para controlar el menú contextual (⋮)
    var expanded by remember { mutableStateOf(false) }

    // Estado para mostrar/ocultar los botones de mover (⬆️⬇️)
    var showMoveButtons by remember { mutableStateOf(false) }


    // Estados locales para los textos del ítem (se actualizan y luego se propagan al salir del foco)
    var localTitle by remember(item.id) { mutableStateOf(item.title) }
    var localAction by remember(item.id) { mutableStateOf(item.action) }

    Card(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.clickable { onToggleChecked() }.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                ItemTextField(
                    value = localTitle,
                    onValueChange = { localTitle = it },
                    onFocusLost = {
                        if (localTitle != item.title) onTitleChange(localTitle)
                    },
                    label = stringResource(R.string.checklistitemcard_outlinedtextfield_item),
                    modifier = Modifier.weight(1.5f),
                    backgroundColor = backgroundColor
                )

                ItemTextField(
                    value = localAction,
                    onValueChange = { localAction = it },
                    onFocusLost = {
                        if (localAction != item.action) onActionChange(localAction)
                    },
                    label = stringResource(R.string.checklistitemcard_outlinedtextfield_action),
                    modifier = Modifier.weight(1.5f),
                    backgroundColor = backgroundColor
                )

                if (showMoveButtons) {
                    MoveButtons(onMoveUp, onMoveDown)
                }

                Column(horizontalAlignment = Alignment.End) {
                    ItemSideIcons(
                        hasInfo = !item.infoTitle.isNullOrBlank() || !item.infoBody.isNullOrBlank(),
                        hasImage = !item.imageUri.isNullOrBlank(),
                        onViewInfoClick = onViewInfoClick,
                        onViewImageClick = onViewImageClick
                    )

                    ItemContextMenu(
                        expanded = expanded,
                        onDismiss = { expanded = false },
                        onMoveToggle = {
                            expanded = false
                            showMoveButtons = !showMoveButtons
                        },
                        onDelete = {
                            expanded = false
                            onDeleteItem()
                        },
                        onAddInfo = {
                            expanded = false
                            onAddInfoClick()
                        },
                        onAddImage = {
                            expanded = false
                            onAddImageClick()
                        },
                        onToggleImportant = {
                            expanded = false
                            onToggleImportant()
                        },
                        isImportant = item.backgroundColorHex == "#FFF59D",
                        onMenuOpen = { expanded = true }
                    )
                }
            }
        }
    }
}
