package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.domain.player.FlatPlayback
import com.sergiom.flycheck.domain.player.ItemStatus

/**
 * ## DisplayerItemList
 *
 * **Propósito**: Mostrar la lista de ítems de la sección actual.
 * Reutiliza el helper compartido [buildSectionRows] para construir:
 * - Cabeceras de subsección (cuando cambia el path).
 * - Ítems operables con su índice global.
 */
@Composable
internal fun DisplayerItemList(
    flat: FlatPlayback?,
    sectionIndex: Int,
    statuses: Map<String, ItemStatus>,
    onRowToggle: (itemId: String, globalIndex: Int) -> Unit,
    onShowInfo: (title: String?, body: String?) -> Unit,
    onShowImage: (uri: String, title: String?, desc: String?) -> Unit
) {
    // Derivamos las filas desde el helper único (memoizado por flat + sección)
    val rows = remember(flat, sectionIndex) { buildSectionRows(flat, sectionIndex) }

    if (rows.isEmpty()) {
        EmptyChecklistCard()
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(rows, key = { it.key }) { row ->
            when (row) {
                is DisplayerRowItem.SubsectionHeader -> SubsectionHeader(row.title)
                is DisplayerRowItem.CheckItem -> {
                    val item = row.ref.itemBlock.item
                    val id = item.id
                    val isDone = statuses[id] == ItemStatus.DONE
                    ItemRowCard(
                        title = item.title,
                        action = item.action,
                        emphasisHex = item.backgroundColorHex,
                        isDone = isDone,
                        hasInfo = !item.infoTitle.isNullOrBlank() || !item.infoBody.isNullOrBlank(),
                        hasImage = !item.imageUri.isNullOrBlank(),
                        onClick = { onRowToggle(id, row.globalIndex) },
                        onInfoClick = {
                            if (!item.infoTitle.isNullOrBlank() || !item.infoBody.isNullOrBlank()) {
                                onShowInfo(item.infoTitle, item.infoBody)
                            }
                        },
                        onImageClick = {
                            if (!item.imageUri.isNullOrBlank()) {
                                onShowImage(item.imageUri, item.imageTitle, item.imageDescription)
                            }
                        }
                    )
                }
            }
        }
    }
}

/* ---- Subcomposables ligeros ---- */

/** Tarjeta de placeholder para secciones sin ítems. */
@Composable
internal fun EmptyChecklistCard() {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Checklist vacía",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Esta plantilla no contiene ítems. Vuelve al editor y añade elementos, o abre otra checklist.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/** Cabecera visual para una subsección dentro de la lista. */
@Composable
internal fun SubsectionHeader(title: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        tonalElevation = 0.9.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
            )
        }
    }
}
