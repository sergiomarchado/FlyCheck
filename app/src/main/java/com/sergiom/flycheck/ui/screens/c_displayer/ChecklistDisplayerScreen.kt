package com.sergiom.flycheck.ui.screens.c_displayer

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiom.flycheck.data.models.DisplayerUiState
import com.sergiom.flycheck.domain.player.FlatPlayback
import com.sergiom.flycheck.domain.player.ItemStatus
import com.sergiom.flycheck.viewmodel.theme.ThemeViewModel
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.DisplayerHeaderCard
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.DisplayerItemList
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.DisplayerProgress
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.DisplayerTopBar
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.EmptyChecklistCard
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.ImageDialog
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.InfoDialog
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.ItemRowCard
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.SectionPickerSheet
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.SectionTabs
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.SubsectionHeader
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.buildSectionRows
import com.sergiom.flycheck.ui.screens.c_displayer.components.displayer.DisplayerRowItem

/**
 * # ChecklistDisplayerScreen
 *
 * **Propósito**: Pantalla principal del Displayer para ejecutar una checklist.
 * Orquesta la UI (Material 3 + Compose) para:
 * - Mostrar metadatos del template (modelo, aerolínea, logo).
 * - Navegar entre **secciones** y **subsecciones** (tabs + bottom sheet selector).
 * - Visualizar el **progreso** de la sección actual.
 * - Listar y marcar ítems, mostrando info contextual (diálogos de texto/imagen).
 *
 * **Entradas**:
 * - `state`: estado de presentación (títulos, índices de sección, conteos…).
 * - `flat`: representación lineal (flattened) de la checklist.
 * - `statuses`: mapa de estado por ítem (`DONE` / no `DONE`).
 *
 * **Callbacks**:
 * - `onToggleItem(id)`: alterna el estado de un ítem.
 * - `onJumpToItem(globalIndex)`: salta al ítem global indicado (post toggle).
 * - `onSelectSection(index)`: cambia de sección.
 * - `onBack()`: navegación hacia atrás.
 *
 * **Adaptativo**:
 * - En **móvil apaisado compacto** (alto < 480dp) usa layout con **header scrolleable** y **tabs sticky**.
 * - En otros tamaños, layout columnar “normal”.
 */
@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChecklistDisplayerScreen(
    state: DisplayerUiState,
    flat: FlatPlayback?,
    statuses: Map<String, ItemStatus>,
    onToggleItem: (String) -> Unit,
    onJumpToItem: (Int) -> Unit,
    onSelectSection: (Int) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState? = null
) {
    // ---- Estado local de la pantalla (solo UI) ----
    var showSectionPicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var infoDialog by remember { mutableStateOf<Pair<String?, String?>?>(null) }
    var imageDialog by remember { mutableStateOf<Triple<String, String?, String?>?>(null) }

    // Modo “móvil paisaje compacto” para cambiar layout
    val configuration = LocalConfiguration.current
    val isCompactLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenHeightDp < 480

    // Tema: integración con ThemeViewModel para alternar modo desde la top bar
    val themeVm: ThemeViewModel = hiltViewModel()
    val mode by themeVm.mode.collectAsState()

    Scaffold(
        topBar = {
            DisplayerTopBar(
                title = "Reproductor de Checklist",
                onBack = onBack,
                onOpenSections = { showSectionPicker = true },
                sectionsEnabled = state.sectionSummaries.isNotEmpty(),
                themeMode = mode,
                onToggleTheme = { themeVm.cycle() }
            )
        },
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } }
    ) { padding ->

        if (isCompactLandscape) {
            // ======= MÓVIL HORIZONTAL COMPACTO: header scrolleable + tabs sticky =======
            CompactLandscapeContent(
                paddingValues = padding,
                state = state,
                flat = flat,
                statuses = statuses,
                onSelectSection = onSelectSection,
                onRowToggle = { id, gi -> onToggleItem(id); onJumpToItem(gi) },
                onShowInfo = { t, b -> infoDialog = t to b },
                onShowImage = { uri, t, d -> imageDialog = Triple(uri, t, d) }
            )
        } else {
            // ======= LAYOUT NORMAL (tablet y móvil vertical) =======
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Cabecera con datos del template (si hay checklist cargada)
                if (flat != null) {
                    DisplayerHeaderCard(
                        title = state.title,
                        model = state.model,
                        airline = state.airline,
                        includeLogo = flat.template.includeLogo,
                        logoUri = flat.template.logoUri
                    )
                }

                // Tabs de secciones
                SectionTabs(
                    summaries = state.sectionSummaries,
                    selectedIndex = state.currentSectionIndex,
                    onSelect = onSelectSection
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Progreso de la sección actual
                DisplayerProgress(
                    sectionDone = state.sectionDone,
                    sectionTotal = state.sectionTotal,
                    sectionTitle = state.sectionTitle
                )

                // Lista de ítems de la sección actual (incluye subsecciones)
                DisplayerItemList(
                    flat = flat,
                    sectionIndex = state.currentSectionIndex,
                    statuses = statuses,
                    onRowToggle = { id, gi ->
                        onToggleItem(id)
                        onJumpToItem(gi)
                    },
                    onShowInfo = { title, body -> infoDialog = title to body },
                    onShowImage = { uri, title, desc -> imageDialog = Triple(uri, title, desc) }
                )
            }
        }
    }

    // ----- BottomSheet selector de secciones/subsecciones -----
    if (showSectionPicker) {
        ModalBottomSheet(
            onDismissRequest = { showSectionPicker = false },
            sheetState = sheetState
        ) {
            SectionPickerSheet(
                summaries = state.sectionSummaries,
                currentIndex = state.currentSectionIndex,
                subsections = state.currentSectionSubsections,
                onPickSection = {
                    onSelectSection(it)
                    showSectionPicker = false
                },
                onPickSubsection = { gi ->
                    onJumpToItem(gi)
                    showSectionPicker = false
                }
            )
        }
    }

    // ----- Diálogos contextuales -----
    infoDialog?.let { (title, body) ->
        InfoDialog(title = title, body = body, onDismiss = { infoDialog = null })
    }
    imageDialog?.let { (uri, title, desc) ->
        ImageDialog(uri = uri, title = title, desc = desc, onDismiss = { imageDialog = null })
    }
}

/* ====================== Compact Landscape ====================== */

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompactLandscapeContent(
    paddingValues: PaddingValues,
    state: DisplayerUiState,
    flat: FlatPlayback?,
    statuses: Map<String, ItemStatus>,
    onSelectSection: (Int) -> Unit,
    onRowToggle: (String, Int) -> Unit,
    onShowInfo: (String?, String?) -> Unit,
    onShowImage: (String, String?, String?) -> Unit
) {
    // Recalcula solo si cambian `flat` o la sección actual
    val rows: List<DisplayerRowItem> = remember(flat, state.currentSectionIndex) {
        buildSectionRows(flat, state.currentSectionIndex)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header Card (scrolleable)
        item(key = "header") {
            if (flat != null) {
                DisplayerHeaderCard(
                    title = state.title,
                    model = state.model,
                    airline = state.airline,
                    includeLogo = flat.template.includeLogo,
                    logoUri = flat.template.logoUri
                )
            }
        }

        // Tabs + Progreso (sticky)
        stickyHeader(key = "tabs") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(Modifier.padding(vertical = 8.dp)) {
                    SectionTabs(
                        summaries = state.sectionSummaries,
                        selectedIndex = state.currentSectionIndex,
                        onSelect = onSelectSection
                    )
                    DisplayerProgress(
                        sectionDone = state.sectionDone,
                        sectionTotal = state.sectionTotal,
                        sectionTitle = state.sectionTitle
                    )
                }
            }
        }

        // Lista: subsecciones + ítems
        if (rows.isEmpty()) {
            item(key = "empty") { EmptyChecklistCard() }
        } else {
            items(rows, key = { it.key }) { row ->
                when (row) {
                    is DisplayerRowItem.SubsectionHeader -> SubsectionHeader(row.title)
                    is DisplayerRowItem.CheckItem -> {
                        val item = row.ref.itemBlock.item
                        val isDone = statuses[item.id] == ItemStatus.DONE
                        ItemRowCard(
                            title = item.title,
                            action = item.action,
                            emphasisHex = item.backgroundColorHex,
                            isDone = isDone,
                            hasInfo = !item.infoTitle.isNullOrBlank() || !item.infoBody.isNullOrBlank(),
                            hasImage = !item.imageUri.isNullOrBlank(),
                            onClick = { onRowToggle(item.id, row.globalIndex) },
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
}
