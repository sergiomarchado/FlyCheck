package com.sergiom.flycheck.viewmodel.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.data.models.DisplayerUiState
import com.sergiom.flycheck.data.models.SectionSummary
import com.sergiom.flycheck.data.models.SubsectionSummary
import com.sergiom.flycheck.domain.player.ChecklistPlayer
import com.sergiom.flycheck.domain.player.FlatPlayback
import com.sergiom.flycheck.domain.player.ItemStatus
import com.sergiom.flycheck.ui.common.SUBSECTION_SEP
import com.sergiom.flycheck.viewmodel.player.index.SectionIndex
import com.sergiom.flycheck.viewmodel.player.index.buildSectionIndex
import com.sergiom.flycheck.viewmodel.player.index.buildSectionSummaries
import com.sergiom.flycheck.viewmodel.player.index.buildSubsectionSummaries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ChecklistDisplayerViewModel @Inject constructor(
    private val player: ChecklistPlayer,
    private val json: Json
) : ViewModel() {

    private var currentTemplateId: String? = null

    // Índice precalculado (se reconstruye cuando cambia la plantilla/flat)
    private val sectionIndexFlow = MutableStateFlow<SectionIndex?>(null)

    /** Flat “aplastado”: estructura lineal + metadatos de secciones/subsecciones. */
    val flat: StateFlow<FlatPlayback?> =
        player.state
            .map { it?.flat }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Estados por itemId (DONE/PENDING) para pintar filas en UI. */
    val statuses: StateFlow<Map<String, ItemStatus>> =
        player.state
            .map { it?.statuses ?: emptyMap() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    /** Eventos efímeros (snackbars, etc.). */
    private val _events = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val events: SharedFlow<UiEvent> = _events

    init {
        // Reindexa cuando llega un nuevo flat (p.ej., al cargar una plantilla).
        viewModelScope.launch {
            flat.filterNotNull().collect { f -> sectionIndexFlow.value = buildSectionIndex(f) }
        }
    }

    /**
     * Estado de UI derivado del reproductor.
     * Se hace el mapeo “pesado” en Dispatcher de CPU y usando el índice precalculado.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DisplayerUiState> =
        player.state
            .filterNotNull()
            .mapLatest { s ->
                withContext(Dispatchers.Default) {
                    val index = sectionIndexFlow.value ?: buildSectionIndex(s.flat)

                    // 1) Summaries de SECCIONES (rápido gracias al índice)
                    val summaries: List<SectionSummary> =
                        buildSectionSummaries(
                            index = index,
                            items = s.flat.items,
                            sectionTitles = s.flat.sectionTitles,
                            statuses = s.statuses
                        )

                    // 2) Checklist vacía → solo metadatos + tabs
                    if (s.total == 0) {
                        return@withContext DisplayerUiState(
                            title = s.flat.template.name,
                            model = s.flat.template.aircraftModel,
                            airline = s.flat.template.airline,
                            sectionTitles = s.flat.sectionTitles,
                            sectionSummaries = summaries
                        )
                    }

                    // Ítem actual y sección activa
                    val ref = s.current
                    val item = ref.itemBlock.item
                    val sectionIndex = ref.sectionIndex
                    val sectionTitle = s.flat.sectionTitles.getOrNull(sectionIndex).orEmpty()

                    // 3) Subsecciones de la sección actual (también vía índice)
                    val subsections: List<SubsectionSummary> =
                        buildSubsectionSummaries(
                            index = index,
                            items = s.flat.items,
                            statuses = s.statuses,
                            sectionIndex = sectionIndex
                        )

                    // Subruta actual (si existe) y subsección actual
                    val currentSubPath = ref.subsectionTitles.joinToString(SUBSECTION_SEP).takeIf { it.isNotBlank() }
                    val currentSub = currentSubPath?.let { p -> subsections.firstOrNull { it.breadcrumb == p } }

                    // 4) Progreso de la sección actual
                    val sectionSummary = summaries.getOrNull(sectionIndex)
                    val doneInSection = sectionSummary?.done ?: 0
                    val totalInSection = sectionSummary?.total ?: 0

                    // 5) Estado final para UI
                    DisplayerUiState(
                        title = s.flat.template.name,
                        model = s.flat.template.aircraftModel,
                        airline = s.flat.template.airline,

                        sectionTitle = sectionTitle,
                        sectionDone = doneInSection,
                        sectionTotal = totalInSection,

                        subsectionPath = ref.subsectionTitles.joinToString(SUBSECTION_SEP),
                        itemTitle = item.title,
                        itemAction = item.action,
                        itemId = item.id,
                        itemStatus = s.statuses[item.id] ?: ItemStatus.PENDING,
                        progress = s.progress.coerceIn(0f, 1f), // defensivo
                        total = s.total,
                        index = ref.globalIndex + 1,           // 1-based
                        emphasisColorHex = item.backgroundColorHex,
                        infoTitle = item.infoTitle,
                        infoBody = item.infoBody,
                        imageUri = item.imageUri,
                        imageTitle = item.imageTitle,
                        imageDescription = item.imageDescription,

                        sectionTitles = s.flat.sectionTitles,
                        currentSectionIndex = sectionIndex,
                        sectionSummaries = summaries,
                        currentSectionSubsections = subsections,
                        currentSubsection = currentSub
                    )
                }
            }
            .distinctUntilChanged() // evita recomposiciones si no cambia el estado
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DisplayerUiState())

    // ----------------- Entrada de datos / control del reproductor -----------------

    fun initWithTemplate(template: CheckListTemplateModel) {
        // Evita recargar si ya está el mismo template
        if (currentTemplateId == template.id) return
        currentTemplateId = template.id
        player.load(template)
    }

    fun initWithTemplateJson(jsonString: String) {
        runCatching {
            json.decodeFromString(CheckListTemplateModel.serializer(), jsonString)
        }.onSuccess { template ->
            initWithTemplate(template)
        }.onFailure {
            it.printStackTrace()
            _events.tryEmit(UiEvent.ShowMessage("No se pudo abrir la checklist"))
        }
    }

    fun reset() {
        currentTemplateId = null
        player.reset()
    }

    // Navegación / acciones
    fun onJumpToSection(i: Int) = player.jumpToSection(i)

    fun onToggleItem(itemId: String) = player.toggleById(itemId)

    fun onJumpToItem(index: Int) {
        // Defensivo: si llega un índice fuera de rango desde la UI, lo ignoramos educadamente.
        val size = flat.value?.items?.size ?: 0
        if (index in 0 until size) {
            player.jumpTo(index)
        }
    }
}
