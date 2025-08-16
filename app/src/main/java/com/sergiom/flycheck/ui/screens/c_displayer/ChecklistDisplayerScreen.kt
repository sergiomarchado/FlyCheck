package com.sergiom.flycheck.ui.screens.c_displayer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sergiom.flycheck.R
import com.sergiom.flycheck.domain.player.FlatPlayback
import com.sergiom.flycheck.domain.player.ItemRef
import com.sergiom.flycheck.domain.player.ItemStatus
import com.sergiom.flycheck.presentation.viewmodel.player.DisplayerUiState
import com.sergiom.flycheck.presentation.viewmodel.player.SectionSummary
import com.sergiom.flycheck.ui.common.ITEM_COMPLETED_COLOR
import com.sergiom.flycheck.ui.common.ITEM_COMPLETED_SCALE
import com.sergiom.flycheck.ui.common.ITEM_DEFAULT_SCALE
import com.sergiom.flycheck.ui.screens.b_editor.components.editor.header.CheckListHeaderCard
import com.sergiom.flycheck.ui.theme.Shape
import com.sergiom.flycheck.ui.theme.parseHexPerson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistDisplayerScreen(
    state: DisplayerUiState,
    flat: FlatPlayback?,                 // <- del VM
    statuses: Map<String, ItemStatus>,   // <- del VM
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToggle: () -> Unit,
    onToggleItem: (String) -> Unit,
    onJumpToItem: (Int) -> Unit,
    onSelectSection: (Int) -> Unit,
    onBack: () -> Unit
) {
    // BottomSheet de selección de secciones
    val openSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (openSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { openSheet.value = false },
            sheetState = sheetState
        ) {
            SectionPickerSheet(
                summaries = state.sectionSummaries,
                currentIndex = state.currentSectionIndex,
                onPick = {
                    onSelectSection(it)
                    openSheet.value = false
                }
            )
        }
    }

    // Diálogos
    var infoDialog by remember { mutableStateOf<InfoPayload?>(null) }
    var imageDialog by remember { mutableStateOf<ImagePayload?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checklist") }, // el título grande lo pone la card de cabecera
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { openSheet.value = true },
                        enabled = state.sectionSummaries.isNotEmpty()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Secciones")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // === Cabecera estilo editor ===
            if (flat != null) {
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
                        CheckListHeaderCard(
                            name = state.title,
                            model = state.model,
                            airline = state.airline,
                            includeLogo = flat.template.includeLogo,
                            logoUri = flat.template.logoUri
                        )
                    }
                }
            }

            // Tabs/segmented de secciones
            if (state.sectionTitles.isNotEmpty()) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    state.sectionTitles.forEachIndexed { i, title ->
                        SegmentedButton(
                            selected = i == state.currentSectionIndex,
                            onClick = { onSelectSection(i) },
                            label = { Text(title.take(16)) },
                            shape = Shape.small
                        )
                    }
                }
            }

            // Progreso global + breadcrumb
            if (state.total > 0) {
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "${state.index}/${state.total} • ${state.sectionTitle}${
                        if (state.subsectionPath.isNotBlank()) " • ${state.subsectionPath}" else ""
                    }",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // === Lista de ítems de la sección actual ===
            val sectionItems = remember(flat, state.currentSectionIndex) {
                flat.sectionItemsOf(state.currentSectionIndex)
            }

            if (sectionItems.isEmpty()) {
                EmptyChecklistCard()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    var lastSubPath: List<String> = emptyList()
                    itemsIndexed(sectionItems, key = { _, pair -> pair.first }) { _, (globalIndex, ref) ->
                        val item = ref.itemBlock.item
                        val id = item.id
                        val status = statuses[id] ?: ItemStatus.PENDING

                        if (ref.subsectionTitles != lastSubPath) {
                            lastSubPath = ref.subsectionTitles
                            if (ref.subsectionTitles.isNotEmpty()) {
                                SubsectionHeader(ref.subsectionTitles.joinToString(" • "))
                            }
                        }

                        ItemRowCard(
                            title = item.title,
                            action = item.action,
                            emphasisHex = item.backgroundColorHex,
                            isDone = status == ItemStatus.DONE,
                            hasInfo = !item.infoTitle.isNullOrBlank() || !item.infoBody.isNullOrBlank(),
                            hasImage = !item.imageUri.isNullOrBlank(),
                            onClick = {
                                onToggleItem(id)
                                onJumpToItem(globalIndex)
                            },
                            onInfoClick = {
                                if (!item.infoTitle.isNullOrBlank() || !item.infoBody.isNullOrBlank()) {
                                    infoDialog = InfoPayload(item.infoTitle, item.infoBody)
                                }
                            },
                            onImageClick = {
                                if (!item.imageUri.isNullOrBlank()) {
                                    imageDialog = ImagePayload(
                                        uri = item.imageUri,
                                        title = item.imageTitle,
                                        desc = item.imageDescription
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo info
    infoDialog?.let { p ->
        AlertDialog(
            onDismissRequest = { infoDialog = null },
            title = { if (!p.title.isNullOrBlank()) Text(p.title) },
            text = { if (!p.body.isNullOrBlank()) Text(p.body) },
            confirmButton = {
                TextButton(onClick = { infoDialog = null }) { Text("Cerrar") }
            }
        )
    }

    // Diálogo imagen
    imageDialog?.let { p ->
        AlertDialog(
            onDismissRequest = { imageDialog = null },
            title = { if (!p.title.isNullOrBlank()) Text(p.title) },
            text = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = p.uri,
                        contentDescription = p.desc,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    )
                    if (!p.desc.isNullOrBlank()) {
                        Text(p.desc, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { imageDialog = null }) { Text("Cerrar") }
            }
        )
    }
}

/* ---------- Helpers & subcomponentes ---------- */

private fun FlatPlayback?.sectionItemsOf(sectionIndex: Int): List<Pair<Int, ItemRef>> {
    if (this == null) return emptyList()
    val result = mutableListOf<Pair<Int, ItemRef>>()
    items.forEachIndexed { gi, ref ->
        if (ref.sectionIndex == sectionIndex) result += gi to ref
    }
    return result
}

@Composable
private fun EmptyChecklistCard() {
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
private fun SubsectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
fun ItemRowCard(
    title: String,
    action: String,
    emphasisHex: String,
    isDone: Boolean,
    hasInfo: Boolean,
    hasImage: Boolean,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseColor = parseHexPerson(emphasisHex)

    // Animaciones
    val bgColor by animateColorAsState(
        targetValue = if (isDone) ITEM_COMPLETED_COLOR else baseColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "bg"
    )
    val scale by animateFloatAsState(
        targetValue = if (isDone) ITEM_COMPLETED_SCALE else ITEM_DEFAULT_SCALE,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(shape)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDone) 6.dp else 2.dp),
        shape = shape,
        border = if (isDone) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Título + acción en una sola fila
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Medium,
                    softWrap = true,
                    modifier = Modifier.weight(1f, fill = true)
                )

                if (action.isNotBlank()) {
                    AssistChip(
                        onClick = onClick, // mismo gesto: marcar/alternar
                        label = { Text(action) },
                        enabled = true
                    )
                }
            }

            // Acciones secundarias a la derecha
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasInfo) {
                    IconButton(onClick = onInfoClick) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Información adicional",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (hasImage) {
                    IconButton(onClick = onImageClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_image),
                            contentDescription = "Item con imagen y nota informativa.",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun SectionPickerSheet(
    summaries: List<SectionSummary>,
    currentIndex: Int,
    onPick: (Int) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Secciones",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        summaries.forEachIndexed { index, s ->
            ElevatedCard(
                onClick = { onPick(index) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(s.title.ifBlank { "Sección ${index + 1}" }, fontWeight = FontWeight.SemiBold)
                    LinearProgressIndicator(
                        progress = {
                            if (s.total == 0) 0f else s.done.toFloat() / s.total.toFloat()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("${s.done} / ${s.total}", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
/* ---------- Payloads diálogos ---------- */

private data class InfoPayload(val title: String?, val body: String?)
private data class ImagePayload(val uri: String, val title: String?, val desc: String?)
