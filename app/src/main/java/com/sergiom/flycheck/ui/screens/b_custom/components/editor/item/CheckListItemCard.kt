package com.sergiom.flycheck.ui.screens.b_custom.components.editor.item

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListItemModel
import com.sergiom.flycheck.util.ITEM_COMPLETED_COLOR
import com.sergiom.flycheck.util.ITEM_COMPLETED_SCALE
import com.sergiom.flycheck.util.ITEM_DEFAULT_BACKGROUND_COLOR_DARK
import com.sergiom.flycheck.util.ITEM_DEFAULT_BACKGROUND_COLOR_LIGHT
import com.sergiom.flycheck.util.ITEM_DEFAULT_SCALE
import com.sergiom.flycheck.util.flyCheckOutlinedTextFieldColorsFor

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
    onViewInfoClick:() -> Unit,
    onAddImageClick: () -> Unit,
    onViewImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determinar si el tema actual es oscuro para ajustar el color de fondo por defecto
    val isDark = isSystemInDarkTheme()
    val defaultBackgroundColor =
        if (isDark) ITEM_DEFAULT_BACKGROUND_COLOR_DARK else ITEM_DEFAULT_BACKGROUND_COLOR_LIGHT

    // Determinar el color de fondo final del ítem en función de si está completado
    val backgroundColor = if (item.completed) ITEM_COMPLETED_COLOR else defaultBackgroundColor

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
        Box(
            modifier = Modifier
                .clickable { onToggleChecked() }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // CAMPO DE TÍTULO DEL ÍTEM
                OutlinedTextField(
                    value = localTitle,
                    onValueChange = { localTitle = it },
                    label = { Text(stringResource(R.string.checklistitemcard_outlinedtextfield_item)) },
                    modifier = Modifier
                        .weight(1f)   // ocupa la mitad del ancho disponible
                        .onFocusChanged { focus ->
                            // Al salir del foco, si el texto ha cambiado, notificamos el cambio
                            if (!focus.isFocused && localTitle != item.title) {
                                onTitleChange(localTitle)
                            }
                        },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = false,
                    minLines = 1,
                    maxLines = 4,
                    colors = flyCheckOutlinedTextFieldColorsFor(backgroundColor)
                )

                // CAMPO DE TEXTO DE LA ACCIÓN DEL ÍTEM
                OutlinedTextField(
                    value = localAction,
                    onValueChange = { localAction = it },
                    label = { Text(stringResource(R.string.checklistitemcard_outlinedtextfield_action)) },
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { focus ->
                            // Si deja de estar focus se notifica de nuevo el cambio de texto
                            if (!focus.isFocused && localAction != item.action) {
                                onActionChange(localAction)
                            }
                        },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = false,
                    minLines = 1,
                    maxLines = 4,
                    colors = flyCheckOutlinedTextFieldColorsFor(backgroundColor)
                )

                // BOTONES DE MOVER (visibles solo si showMoveButtons == true)
                if (showMoveButtons) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        IconButton(onClick = onMoveUp) {
                            Icon(
                                imageVector= Icons.Default.KeyboardArrowUp,
                                contentDescription = stringResource(R.string.checklistitemcard_button_move_up))
                        }
                        IconButton(onClick = onMoveDown) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.checklistitemcard_button_move_down))
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {

                    // ℹ️ Icono de info si hay contenido
                    if (!item.infoTitle.isNullOrBlank() || !item.infoBody.isNullOrBlank()) {
                        IconButton(onClick = onViewInfoClick) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(R.string.checklistitemcard_icon_info_contentdescription),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // 🖼️ Icono de imagen si hay imagen
                    if (!item.imageUri.isNullOrBlank()) {
                        IconButton(onClick = onViewImageClick) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add_image),
                                contentDescription = stringResource(R.string.checklistitemcard_icon_image_contentdescription),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Menú contextual
                    Box {
                        IconButton(onClick = { expanded = true }) {

                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.checklistitemcard_icon_moreopcions),
                                tint = Color.DarkGray
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.checklistitemcard_context_menu_move)) },
                                onClick = {
                                    expanded = false
                                    showMoveButtons = !showMoveButtons
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.checklistitemcard_menu_deleteitem)) },
                                onClick = {
                                    expanded = false
                                    onDeleteItem()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.checklistitemcard_context_menu_add_info)) },
                                onClick = {
                                    expanded = false
                                    onAddInfoClick()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.checklistitemcard_context_menu_add_image)) },
                                onClick = {
                                    expanded = false
                                    onAddImageClick()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
