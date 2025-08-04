package com.sergiom.flycheck.ui.screens.b_custom.components.editor.subsection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
 * Componente visual que representa el encabezado de una sub-sección dentro del editor.
 * Muestra el título de la sub-sección y un menú contextual con acciones como renombrar, eliminar o mover.
 *
 * @param title Título de la sub-sección
 * @param onRenameClick Acción a ejecutar cuando el usuario elige renombrar
 * @param onDeleteClick Acción a ejecutar cuando el usuario elige eliminar
 * @param onMoveUp Acción a ejecutar para mover la sub-sección hacia arriba
 * @param onMoveDown Acción a ejecutar para mover la sub-sección hacia abajo
 * @param modifier Modificador externo para ajustar la apariencia o el comportamiento del componente
 */
@Composable
fun CheckListSubsectionTitleCard(
    title: String,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    modifier: Modifier
) {
    // Estado para mostrar/ocultar el DropdownMenu
    var expanded by remember { mutableStateOf(false) }

    // Estado para mostrar/ocultar los botones de mover arriba/abajo
    var showMoveButtons by remember { mutableStateOf(false) }

    // Contenedor visual con estilo (elevación + color de fondo + esquinas redondeadas)
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.5.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        // Fila principal que contiene el título y los botones del menú
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Título de la sub-sección
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier
                    .weight(1f)  // Ocupa el espacio restante disponible
                    .padding(12.dp)
            )

            // Fila que contiene botones de mover (condicional) y el menú contextual
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botones de mover arriba/abajo (solo visibles si showMoveButtons == true)
                if (showMoveButtons) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        IconButton(
                            onClick = onMoveUp,
                            modifier = Modifier
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Mover hacia arriba"
                            )
                        }
                        IconButton(
                            onClick = onMoveDown,
                            modifier = Modifier
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Mover hacia abajo"
                            )
                        }
                    }
                }

                // Icono Menú contextual
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.checklistsectionheader_menu_contentdescription),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Menú desplegable con acciones disponibles
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // Acción: Renombrar
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.checklistsectionheader_menu_rename)) },
                            onClick = {
                                expanded = false
                                onRenameClick()
                            }
                        )
                        // Acción: Eliminar
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.checklistsectionheader_menu_delete)) },
                            onClick = {
                                expanded = false
                                onDeleteClick()
                            }
                        )
                        // Acción: Mostrar/Ocultar botones de mover
                        DropdownMenuItem(
                            text = { Text("Mover") },
                            onClick = {
                                expanded = false
                                showMoveButtons = !showMoveButtons
                            }
                        )
                    }
                }
            }
        }
    }
}
