package com.sergiom.flycheck.presentation.viewmodel.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.domain.player.ChecklistPlayer
import com.sergiom.flycheck.domain.player.ItemStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json

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

    private var inMemoryTemplate: CheckListTemplateModel? = null

    val uiState: StateFlow<DisplayerUiState> =
        player.state.filterNotNull().map { s ->
            // Construir resúmenes por sección
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
                            add(SectionSummary(
                                title = titles.getOrNull(i).orEmpty(),
                                done = done,
                                total = total
                            ))
                        }
                    }
                }
            }

            // 🔒 Caso vacío: no acceder a s.current
            if (s.total == 0) {
                return@map DisplayerUiState(
                    title = s.flat.template.name,
                    model = s.flat.template.aircraftModel,
                    airline = s.flat.template.airline,
                    sectionTitle = "",
                    subsectionPath = "",
                    itemTitle = "",
                    itemAction = "",
                    itemId = "",
                    itemStatus = ItemStatus.PENDING,
                    progress = 0f,
                    total = 0,
                    index = 0,
                    emphasisColorHex = "#D3D3D3",
                    sectionTitles = s.flat.sectionTitles,
                    currentSectionIndex = 0,
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

    fun initWithTemplate(template: CheckListTemplateModel) {
        if (player.state.value != null) return
        inMemoryTemplate = template
        player.load(template)
    }

    fun initWithTemplateJson(jsonString: String) {
        if (player.state.value != null) return
        runCatching {
            json.decodeFromString(CheckListTemplateModel.serializer(), jsonString)
        }.onSuccess(::initWithTemplate)
            .onFailure { it.printStackTrace() }
    }

    fun onNext() = player.next()
    fun onPrev() = player.prev()
    fun onToggle() = player.toggleCurrentDone()
    fun onJumpToSection(i: Int) = player.jumpToSection(i)
}
