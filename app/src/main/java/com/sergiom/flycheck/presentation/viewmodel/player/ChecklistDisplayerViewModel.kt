package com.sergiom.flycheck.presentation.viewmodel.player

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

data class DisplayerUiState(
    val title: String = "",
    val model: String = "",
    val airline: String = "",
    val sectionTitle: String = "",
    val subsectionPath: String = "",
    val itemTitle: String = "",
    val itemAction: String = "",
    val itemId: String = "",
    val itemStatus: ItemStatus = ItemStatus.PENDING,
    val progress: Float = 0f,
    val total: Int = 0,
    val index: Int = 0,
    val emphasisColorHex: String = "#D3D3D3",
    val infoTitle: String? = null,
    val infoBody: String? = null,
    val imageUri: String? = null,
    val imageTitle: String? = null,
    val imageDescription: String? = null,
    val sectionTitles: List<String> = emptyList(),
    val currentSectionIndex: Int = 0,
    val sectionSummaries: List<SectionSummary> = emptyList()
)

@HiltViewModel
class ChecklistDisplayerViewModel @Inject constructor(
    private val player: ChecklistPlayer,
    private val json: Json
) : ViewModel() {

    /** Para evitar recargar la misma checklist si ya está cargada */
    private var currentTemplateId: String? = null

    /** Exponer el FlatPlayback completo para listas */
    val flat: StateFlow<FlatPlayback?> =
        player.state.map { it?.flat }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Exponer el mapa de estados por itemId para pintar cada fila */
    val statuses: StateFlow<Map<String, ItemStatus>> =
        player.state.map { it?.statuses ?: emptyMap() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    /** Estado actual para la cabecera/tarjeta */
    val uiState: StateFlow<DisplayerUiState> =
        player.state.filterNotNull().map { s ->
            // Resumen por sección
            val summaries: List<SectionSummary> = run {
                val starts = s.flat.sectionStartsAt
                val titles = s.flat.sectionTitles
                if (starts.isEmpty()) emptyList()
                else {
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

            // Caso vacío
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

            DisplayerUiState(
                title = s.flat.template.name,
                model = s.flat.template.aircraftModel,
                airline = s.flat.template.airline,
                sectionTitle = s.flat.sectionTitles.getOrNull(ref.sectionIndex) ?: "",
                subsectionPath = ref.subsectionTitles.joinToString(" • "),
                itemTitle = item.title,
                itemAction = item.action,
                itemId = item.id,
                itemStatus = status,
                progress = s.progress,
                total = s.total,
                index = ref.globalIndex + 1,
                emphasisColorHex = item.backgroundColorHex,
                infoTitle = item.infoTitle,
                infoBody = item.infoBody,
                imageUri = item.imageUri,
                imageTitle = item.imageTitle,
                imageDescription = item.imageDescription,
                sectionTitles = s.flat.sectionTitles,
                currentSectionIndex = ref.sectionIndex,
                sectionSummaries = summaries
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DisplayerUiState())

    /** Inicializa el player con un modelo (solo si cambia el id) */
    fun initWithTemplate(template: CheckListTemplateModel) {
        if (currentTemplateId == template.id) return
        currentTemplateId = template.id
        player.load(template)
    }

    /** Inicializa el player a partir de un JSON serializado */
    fun initWithTemplateJson(jsonString: String) {
        runCatching {
            json.decodeFromString(CheckListTemplateModel.serializer(), jsonString)
        }.onSuccess { template ->
            initWithTemplate(template)
        }.onFailure { it.printStackTrace() }
    }

    /** Limpia el estado (por ejemplo al salir del Displayer) */
    fun reset() {
        currentTemplateId = null
        player.reset()
    }

    // Acciones
    fun onNext() = player.next()
    fun onPrev() = player.prev()
    fun onToggle() = player.toggleCurrentDone()
    fun onJumpToSection(i: Int) = player.jumpToSection(i)

    // Helpers para el listado
    fun onToggleItem(itemId: String) = player.toggleById(itemId)
    fun onJumpToItem(index: Int) = player.jumpTo(index)
}
