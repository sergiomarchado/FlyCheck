package com.sergiom.flycheck.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.model.CheckListItem
import com.sergiom.flycheck.util.toColor

@Composable
fun CheckListItemCard(
    item: CheckListItem,
    onToggleChecked: () -> Unit,
    onTitleChange: (String) -> Unit,
    onActionChange: (String) -> Unit
){
    val backgroundColor by animateColorAsState(
        targetValue = if (item.completed) Color(0xFFB9FBC0) else item.backgroundColorHex.toColor(),
        label = "background"
    )

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
                label = { Text("Item") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = item.action,
                onValueChange = onActionChange,
                label = { Text("Acción") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }
    }

}

