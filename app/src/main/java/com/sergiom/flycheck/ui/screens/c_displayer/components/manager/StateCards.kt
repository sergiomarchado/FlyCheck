package com.sergiom.flycheck.ui.screens.c_displayer.components.manager

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Loading simple. */
@Composable
internal fun LoadingContent() {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        Text("Cargando…", style = MaterialTheme.typography.bodyMedium)
    }
}

/** Tarjeta de error con botón de reintento. */
@Composable
internal fun ErrorCard(error: String, onRetry: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Ha ocurrido un error", style = MaterialTheme.typography.titleMedium)
            Text(error, style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onRetry) { Text("Reintentar") }
            }
        }
    }
}

/** Estado vacío. */
@Composable
internal fun EmptyStateCard() {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("No tienes checklists guardadas aún", style = MaterialTheme.typography.titleMedium)
            Text("Crea o importa una para empezar.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
