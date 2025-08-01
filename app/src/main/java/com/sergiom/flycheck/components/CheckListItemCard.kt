package com.sergiom.flycheck.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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

@Composable
fun CheckListItemCard(
    item: CheckListItemModel,
    onToggleChecked: () -> Unit,
    onTitleChange: (String) -> Unit,
    onActionChange: (String) -> Unit,
    onDeleteItem: () -> Unit
) {

    val isDark = isSystemInDarkTheme()
    val defaultBackgroundColor =
        if (isDark) ITEM_DEFAULT_BACKGROUND_COLOR_DARK else ITEM_DEFAULT_BACKGROUND_COLOR_LIGHT

    val backgroundColor = if (item.completed) ITEM_COMPLETED_COLOR  else defaultBackgroundColor


    val scale by animateFloatAsState(
        targetValue = if(item.completed) ITEM_COMPLETED_SCALE else ITEM_DEFAULT_SCALE,
        label = "scale"
    )

    val haptic = LocalHapticFeedback.current

    //Trigger de la vibración
    LaunchedEffect(item.completed) {
        if(item.completed){
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
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

                // Campo título
                OutlinedTextField(
                    value = item.title,
                    onValueChange = onTitleChange,
                    label = {
                        Text(
                            text = stringResource(R.string.checklistitemcard_outlinedtextfield_item),
                        )
                    },
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = false,
                    minLines = 1,
                    maxLines = 4,
                    colors = flyCheckOutlinedTextFieldColorsFor(backgroundColor)
                )

                // Campo acción
                OutlinedTextField(
                    value = item.action,
                    onValueChange = onActionChange,
                    label = {
                        Text(
                            text = stringResource(R.string.checklistitemcard_outlinedtextfield_action),
                        )
                    },
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = false,
                    minLines = 1,
                    maxLines = 4,
                    colors = flyCheckOutlinedTextFieldColorsFor(backgroundColor)
                )

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
                            text = { Text(stringResource(R.string.checklistitemcard_menu_deleteitem)) },
                            onClick = {
                                expanded = false
                                onDeleteItem()
                            }
                        )
                    }
                }
            }
        }
    }
}


