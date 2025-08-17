package com.sergiom.flycheck.ui.screens.c_displayer.components.displayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sergiom.flycheck.domain.player.FlatPlayback
import com.sergiom.flycheck.domain.player.ItemRef
import com.sergiom.flycheck.domain.player.ItemStatus

/**
 * Lista seccionada: inserta cabeceras de subsección cuando cambia el path.
 * Las cabeceras muestran SOLO el último nivel de la subsección.
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
    val items = remember(flat, sectionIndex) { buildSectionList(flat, sectionIndex) }

    if (items.isEmpty()) {
        EmptyChecklistCard()
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items, key = { it.key }) { row ->
            when (row) {
                is RowItem.SubsectionHeader -> SubsectionHeader(row.title)
                is RowItem.CheckItem -> {
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

/* ---- helpers / subcomposables ---- */

private sealed interface RowItem { val key: String
    data class SubsectionHeader(val title: String, override val key: String) : RowItem
    data class CheckItem(val globalIndex: Int, val ref: ItemRef, override val key: String) : RowItem
}

private fun buildSectionList(flat: FlatPlayback?, sectionIndex: Int): List<RowItem> {
    if (flat == null) return emptyList()

    val raw = mutableListOf<Pair<Int, ItemRef>>()
    flat.items.forEachIndexed { gi, ref ->
        if (ref.sectionIndex == sectionIndex) raw += gi to ref
    }
    if (raw.isEmpty()) return emptyList()

    val result = mutableListOf<RowItem>()
    var lastSubPath: List<String> = emptyList()

    for ((gi, ref) in raw) {
        // cambia de subsección: añade header SOLO con el último nivel
        if (ref.subsectionTitles != lastSubPath && ref.subsectionTitles.isNotEmpty()) {
            lastSubPath = ref.subsectionTitles
            val headerTitle = ref.subsectionTitles.lastOrNull()
            if (headerTitle != null) {
                result += RowItem.SubsectionHeader(
                    title = headerTitle,
                    key = "h:$sectionIndex:$gi"
                )
            }
        }
        result += RowItem.CheckItem(
            globalIndex = gi,
            ref = ref,
            key = "i:$gi:${ref.itemBlock.item.id}"
        )
    }
    return result
}

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

@Composable
internal fun SubsectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp)
    )
}
