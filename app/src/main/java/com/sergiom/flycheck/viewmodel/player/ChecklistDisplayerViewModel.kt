package com.sergiom.flycheck.viewmodel.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.domain.player.ChecklistPlayer
import com.sergiom.flycheck.domain.player.FlatPlayback
import com.sergiom.flycheck.domain.player.ItemStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class SectionSummary(
    val title: String,
    val done: Int,
    val total: Int
)

/** Resumen de subsección con índice global del primer ítem para saltar. */
data class SubsectionSummary(
    val breadcrumb: String,
    val title: String,
    val done: Int,
    val total: Int,
    val firstGlobalIndex: Int
)

data class DisplayerUiState(
    val title: String = "",
    val model: String = "",
    val airline: String = "",

    // Sección actual
    val sectionTitle: String = "",
    val sectionDone: Int = 0,              // ← progreso de la sección actual
    val sectionTotal: Int = 0,             // ← progreso de la sección actual

    // Info de ítem actual
    val subsectionPath: String = "",
    val itemTitle: String = "",
    val itemAction: String = "",
    val itemId: String = "",
    val itemStatus: ItemStatus = ItemStatus.PENDING,
    val progress: Float = 0f,              // progreso global (si te interesa mantenerlo)
    val total: Int = 0,                    // total global
    val index: Int = 0,                    // índice global 1-based
    val emphasisColorHex: String = "#D3D3D3",
    val infoTitle: String? = null,
    val infoBody: String? = null,
    val imageUri: String? = null,
    val imageTitle: String? = null,
    val imageDescription: String? = null,

    // Navegación por secciones
    val sectionTitles: List<String> = emptyList(),
    val currentSectionIndex: Int = 0,
    val sectionSummaries: List<SectionSummary> = emptyList(),

    // Subsecciones de la sección actual
    val currentSectionSubsections: List<SubsectionSummary> = emptyList(),
    val currentSubsection: SubsectionSummary? = null
)

@HiltViewModel
class ChecklistDisplayerViewModel @Inject constructor(
    private val player: ChecklistPlayer,
    private val json: Json
) : ViewModel() {

    private var currentTemplateId: String? = null

    /** Estructura aplanada completa para listas. */
    val flat: StateFlow<FlatPlayback?> =
        player.state.map { it?.flat }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Estados por itemId para pintar cada fila. */
    val statuses: StateFlow<Map<String, ItemStatus>> =
        player.state.map { it?.statuses ?: emptyMap() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    /** Estado de UI derivado del estado del reproductor. */
    val uiState: StateFlow<DisplayerUiState> =
        player.state.filterNotNull().map { s ->
            // --- Resumen por sección ---
            val summaries: List<SectionSummary> = run {
                val starts = s.flat.sectionStartsAt
                val titles = s.flat.sectionTitles
                if (starts.isEmpty()) emptyList() else {
                    val items = s.flat.items
                    buildList {
                        for (i in starts.indices) {
                            val start = starts[i]
                            val end = if (i < starts.lastIndex) starts[i + 1] else items.size
                            val total = (end - start).coerceAtLeast(0)
                            val done = if (total == 0) 0 else {
                                var d = 0
                                for (g in start until end) {
                                    val id = items[g].itemBlock.item.id
                                    if (s.statuses[id] == ItemStatus.DONE) d++
                                }
                                d
                            }
                            add(
                                SectionSummary(
                                    title = titles.getOrNull(i).orEmpty(),
                                    done = done,
                                    total = total
                                )
                            )
                        }
                    }
                }
            }

            // Caso sin ítems
            if (s.total == 0) {
                return@map DisplayerUiState(
                    title = s.flat.template.name,
                    model = s.flat.template.aircraftModel,
                    airline = s.flat.template.airline,
                    sectionTitles = s.flat.sectionTitles,
                    sectionSummaries = summaries
                )
            }

            val ref = s.current
            val item = ref.itemBlock.item
            val status = s.statuses[item.id] ?: ItemStatus.PENDING
            val sectionIndex = ref.sectionIndex
            val sectionTitle = s.flat.sectionTitles.getOrNull(sectionIndex) ?: ""

            // --- Subsecciones de la sección actual (agrupadas por breadcrumb legible) ---
            val pairsInSection = s.flat.items.withIndex().filter { it.value.sectionIndex == sectionIndex }
            val groupedByPath = pairsInSection.groupBy { it.value.subsectionTitles.joinToString(" • ") }
            val subsections = groupedByPath
                .filterKeys { it.isNotBlank() } // ignora path vacío
                .map { (path, list) ->
                    val total = list.size
                    val done = list.count { s.statuses[it.value.itemBlock.item.id] == ItemStatus.DONE }
                    SubsectionSummary(
                        breadcrumb = path,
                        title = path.substringAfterLast(" • ", path),
                        done = done,
                        total = total,
                        firstGlobalIndex = list.minOf { it.index }
                    )
                }
                .sortedBy { it.firstGlobalIndex }

            val currentSubPath = ref.subsectionTitles.joinToString(" • ").takeIf { it.isNotBlank() }
            val currentSub = currentSubPath?.let { p -> subsections.firstOrNull { it.breadcrumb == p } }

            // --- Progreso de la sección actual (lo que quieres mostrar en la barra) ---
            val sectionSummary = summaries.getOrNull(sectionIndex)
            val doneInSection = sectionSummary?.done ?: 0
            val totalInSection = sectionSummary?.total ?: 0

            DisplayerUiState(
                title = s.flat.template.name,
                model = s.flat.template.aircraftModel,
                airline = s.flat.template.airline,

                sectionTitle = sectionTitle,
                sectionDone = doneInSection,
                sectionTotal = totalInSection,

                subsectionPath = ref.subsectionTitles.joinToString(" • "),
                itemTitle = item.title,
                itemAction = item.action,
                itemId = item.id,
                itemStatus = status,
                progress = s.progress,                 // global (lo conservamos por si lo usas)
                total = s.total,
                index = ref.globalIndex + 1,
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
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DisplayerUiState())

    // --- Entrada de datos ---
    fun initWithTemplate(template: CheckListTemplateModel) {
        if (currentTemplateId == template.id) return
        currentTemplateId = template.id
        player.load(template)
    }

    fun initWithTemplateJson(jsonString: String) {
        runCatching {
            json.decodeFromString(CheckListTemplateModel.serializer(), jsonString)
        }.onSuccess { template -> initWithTemplate(template) }
            .onFailure { it.printStackTrace() }
    }

    fun reset() {
        currentTemplateId = null
        player.reset()
    }

    fun onJumpToSection(i: Int) = player.jumpToSection(i)

    // Lista
    fun onToggleItem(itemId: String) = player.toggleById(itemId)
    fun onJumpToItem(index: Int) = player.jumpTo(index)
}
