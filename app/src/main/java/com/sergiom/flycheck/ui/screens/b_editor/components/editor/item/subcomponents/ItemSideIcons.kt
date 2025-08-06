package com.sergiom.flycheck.ui.screens.b_editor.components.editor.item.subcomponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sergiom.flycheck.R

@Composable
fun ItemSideIcons(
    hasInfo: Boolean,
    hasImage: Boolean,
    onViewInfoClick: () -> Unit,
    onViewImageClick: () -> Unit
) {
    if (hasInfo) {
        IconButton(onClick = onViewInfoClick) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.checklistitemcard_icon_info_contentdescription),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (hasImage) {
        IconButton(onClick = onViewImageClick) {
            Icon(
                painter = painterResource(R.drawable.ic_add_image),
                contentDescription = stringResource(R.string.checklistitemcard_icon_image_contentdescription),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


