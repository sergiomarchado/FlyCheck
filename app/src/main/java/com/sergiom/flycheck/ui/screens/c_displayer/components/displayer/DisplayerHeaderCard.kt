package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.header.CheckListHeaderCard
/**
 * Tarjeta de cabecera del displayer.
 *
 * Envuelve y muestra el componente [CheckListHeaderCard], que contiene
 * la información principal de la checklist (nombre, modelo, aerolínea, logo).
 *
 * Uso:
 * - Se coloca en la parte superior de la pantalla de ejecución de checklist.
 * - Se muestra siempre que exista información del template (aunque no haya ítems).
 */
@Composable
internal fun DisplayerHeaderCard(
    title: String,
    model: String,
    airline: String,
    includeLogo: Boolean,
    logoUri: android.net.Uri?
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            // Reutiliza la cabecera ya definida en el editor,
            CheckListHeaderCard(
                name = title,
                model = model,
                airline = airline,
                includeLogo = includeLogo,
                logoUri = logoUri
            )
        }
    }
}
