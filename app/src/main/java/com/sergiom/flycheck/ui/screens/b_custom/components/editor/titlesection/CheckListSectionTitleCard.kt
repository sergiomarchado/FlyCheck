package com.sergiom.flycheck.ui.screens.b_custom.components.editor.titlesection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R

/**
 * Composable que representa el encabezado de una sección en la checklist.
 * Muestra el título de la sección y un menú desplegable con acciones: renombrar o eliminar.
 *
 * @param title Título de la sección que se mostrará en el encabezado.
 * @param onRenameClick Callback que se invoca cuando el usuario selecciona la opción "Renombrar".
 * @param onDeleteClick Callback que se invoca cuando el usuario selecciona la opción "Eliminar".
 */
@Composable
fun CheckListSectionTitleCard(
    title: String,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Estado para controlar si el menú contextual está visible o no
    var expanded by remember { mutableStateOf(false) }

    // Contenedor animado que permite mostrar/ocultar la sección con una transición
    AnimatedVisibility(visible = true) {
        Surface(
            modifier = Modifier
                .fillMaxWidth() // Ocupa el ancho completo del padre
                .padding( vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceTint, // Color de fondo
            tonalElevation = 4.dp,
            shadowElevation = 8.dp,


        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // TÍTULO SECCIÓN
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Botón con menú contextual
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.checklistsectionheader_menu_contentdescription),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Menú desplegable con acciones
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.checklistsectionheader_menu_rename)) },
                            onClick = {
                                expanded = false
                                onRenameClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.checklistsectionheader_menu_delete)) },
                            onClick = {
                                expanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

