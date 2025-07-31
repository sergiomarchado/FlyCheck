package com.sergiom.flycheck.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R
import com.sergiom.flycheck.data.model.CheckListItemModel
import com.sergiom.flycheck.util.toColor

@Composable
fun CheckListItemCard(
    item: CheckListItemModel,
    onToggleChecked: () -> Unit,
    onTitleChange: (String) -> Unit,
    onActionChange: (String) -> Unit,
    onDeleteItem: () -> Unit
){
    val backgroundColor by animateColorAsState(
        targetValue = if (item.completed) Color(0xFFB9FBC0) else item.backgroundColorHex.toColor(),
        label = "background"
    )

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleChecked() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = item.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.checklistitemcard_outlinedtextfield_item)) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = item.action,
                onValueChange = onActionChange,
                label = { Text(stringResource(R.string.checklistitemcard_outlinedtextfield_action)) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Box{
                IconButton(onClick = {expanded = true}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.checklistitemcard_icon_moreopcions)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.checklistitemcard_menu_deleteitem)) },
                        onClick = {
                            onDeleteItem()
                            expanded = false
                        }
                    )
                    // Más opciones del menú en el futuro.
                }

            }


        }
    }

}

