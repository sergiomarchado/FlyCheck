package com.sergiom.flycheck.ui.screens.b_custom.components.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.components.CheckListHeader
import com.sergiom.flycheck.data.model.CheckListTemplateModel

@Composable
fun EditorHeader(template: CheckListTemplateModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 24.dp)
        ) {
            CheckListHeader(
                name = template.name,
                model = template.aircraftModel,
                airline = template.airline,
                includeLogo = template.includeLogo
            )
        }
    }
}