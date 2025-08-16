package com.sergiom.flycheck.ui.screens.c_displayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sergiom.flycheck.domain.player.ItemStatus
import com.sergiom.flycheck.presentation.viewmodel.player.DisplayerUiState
import com.sergiom.flycheck.presentation.viewmodel.player.SectionSummary
import com.sergiom.flycheck.ui.theme.Shape
import com.sergiom.flycheck.ui.theme.parseHexPerson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistDisplayerScreen(
    state: DisplayerUiState,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToggle: () -> Unit,
    onSelectSection: (Int) -> Unit,
    onBack: () -> Unit
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${state.title} • ${state.model}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Abrir selector de secciones
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
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Selector de secciones compacto (puede quedarse además del sheet)
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

            if (state.total == 0) {
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
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = parseHexPerson(state.emphasisColorHex)
                    )
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            state.itemTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (state.itemAction.isNotBlank()) {
                            AssistChip(onClick = {}, label = { Text(state.itemAction) })
                        }

                        if (!state.infoTitle.isNullOrBlank() || !state.infoBody.isNullOrBlank()) {
                            ElevatedCard(Modifier.fillMaxWidth()) {
                                Column(
                                    Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (!state.infoTitle.isNullOrBlank())
                                        Text(state.infoTitle, style = MaterialTheme.typography.titleSmall)
                                    if (!state.infoBody.isNullOrBlank())
                                        Text(state.infoBody)
                                }
                            }
                        }

                        if (!state.imageUri.isNullOrBlank()) {
                            ElevatedCard(Modifier.fillMaxWidth()) {
                                Column(
                                    Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    AsyncImage(model = state.imageUri, contentDescription = state.imageDescription)
                                    if (!state.imageTitle.isNullOrBlank()) Text(state.imageTitle)
                                    if (!state.imageDescription.isNullOrBlank())
                                        Text(
                                            state.imageDescription,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                }
                            }
                        }

                        FilterChip(
                            selected = state.itemStatus == ItemStatus.DONE,
                            onClick = onToggle,
                            label = { Text(if (state.itemStatus == ItemStatus.DONE) "DONE" else "PENDING") }
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onPrev,
                    modifier = Modifier.weight(1f),
                    enabled = state.total > 0
                ) { Text("Prev") }
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    enabled = state.total > 0
                ) { Text("Next") }
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
                modifier = Modifier.fillMaxWidth(),
                colors = if (index == currentIndex)
                    CardDefaults.elevatedCardColors()
                else
                    CardDefaults.elevatedCardColors()
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
