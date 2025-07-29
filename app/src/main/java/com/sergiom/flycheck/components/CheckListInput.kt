package com.sergiom.flycheck.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R

/**
 * Composable que representa el campo de entrada + botón para añadir nuevos ítems
 * a la checklist (por ejemplo: "Encender baterías", "Verificar flaps", etc.).
 *
 * Se trata de una UI simple y reutilizable. No contiene lógica de negocio: solo
 * muestra lo que se le pasa desde fuera.
 */
@Composable
fun CheckListInput(
    text: String,                        // Texto actual del campo de entrada
    onTextChanged: (String) -> Unit,   // Callback cuando el usuario escribe
    onAddItem: () -> Unit                // Callback cuando el usuario pulsa "Agregar"
) {
    Column {
        TextField(
            value = text,
            onValueChange = onTextChanged,
            label = { Text(stringResource(R.string.checklistinput_new_item_textfield_label)) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onAddItem) {
            Text(stringResource(R.string.checklistinput_bottom_add))
        }
    }
}
