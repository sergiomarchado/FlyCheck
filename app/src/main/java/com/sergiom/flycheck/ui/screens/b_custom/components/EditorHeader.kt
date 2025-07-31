package com.sergiom.flycheck.ui.screens.b_custom.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.components.CheckListHeader
import com.sergiom.flycheck.data.model.CheckListTemplateModel

@Composable
fun EditorHeader(template: CheckListTemplateModel) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        CheckListHeader(
            name = template.name,
            model = template.aircraftModel,
            airline = template.airline,
            includeLogo = template.includeLogo
        )
    }
}

