package com.sergiom.flycheck.ui.screens.b_custom.components.editor.header

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.R

/**
 * Cabecera visual que muestra el nombre, modelo, aerolínea y opcionalmente el logo,
 * adaptándose automáticamente al modo vertical u horizontal del dispositivo.
 *
 * @param name Nombre de la plantilla o checklist
 * @param model Modelo de avión o aeronave
 * @param airline Aerolínea asociada
 * @param includeLogo Si es true, se mostrará el logo de la aerolínea
 */
@Composable
fun CheckListHeaderCard(
    name: String,
    model: String,
    airline: String,
    includeLogo: Boolean
) {
    // Detectamos la orientación del dispositivo
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // MODO HORIZONTAL
    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna con nombre y modelo
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

                // NOMBRE
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                // MODELO
                Text(
                    text = model,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            // Fila con aerolínea y logo (si está activado)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = airline,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                if (includeLogo) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ryanair),
                        contentDescription = stringResource(R.string.checklistheader_contentdescription),
                        modifier = Modifier
                            .height(48.dp)
                            .width(48.dp)
                            .padding(start = 4.dp)
                            .clip(RoundedCornerShape(6.dp))  // Logo con bordes redondeados
                    )
                }
            }
        }
    }
    // MODO VERTICAL
    else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fila inferior con aerolínea (y logo) + modelo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Aerolínea y logo (opcional)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (includeLogo) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_ryanair),
                            contentDescription = stringResource(R.string.checklistheader_contentdescription),
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = airline,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                // Modelo del avión
                Text(
                    text = model,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}


