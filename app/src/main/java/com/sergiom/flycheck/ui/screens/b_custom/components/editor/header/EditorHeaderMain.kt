package com.sergiom.flycheck.ui.screens.b_custom.components.editor.header

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.data.model.CheckListTemplateModel

/**
 * Cabecera principal del editor de checklist.
 *
 * Este Composable actúa como un contenedor de nivel superior para la información general
 * de la plantilla (nombre, modelo, aerolínea y logo). Internamente delega el renderizado
 * de los datos a [CheckListHeaderCard].
 *
 * @param template Objeto que contiene la información principal de la checklist actual.
 */
@Composable
fun EditorHeaderMain(template: CheckListTemplateModel) {
    // Se utiliza una Card para dar fondo, bordes y elevación a la cabecera
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer // Color de fondo de la card
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        // Box interno que añade padding y contiene el componente de cabecera
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            // Componente que muestra los datos del encabezado
            CheckListHeaderCard(
                name = template.name,
                model = template.aircraftModel,
                airline = template.airline,
                includeLogo = template.includeLogo,
                logoUri = template.logoUri
            )
        }
    }
}
