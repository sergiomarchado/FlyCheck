package com.sergiom.flycheck.domain.player

import com.sergiom.flycheck.data.models.CheckListTemplateModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.math.max

enum class ItemStatus { PENDING, DONE, SKIPPED }

data class PlaybackState(
    val flat: FlatPlayback,
    val cursor: Int = 0,
    val statuses: Map<String, ItemStatus> = emptyMap(), // key = itemId
    val paused: Boolean = false
) {
    val total: Int get() = flat.items.size
    val current: ItemRef
        get() = flat.items[cursor] // ojo: sólo acceder si total > 0
    val progress: Float
        get() = if (total == 0) 0f else (cursor + 1f) / total
}

interface ChecklistPlayer {
    val state: StateFlow<PlaybackState?>

    fun load(template: CheckListTemplateModel)
    fun reset()

    fun next()
    fun prev()
    fun jumpTo(globalIndex: Int)

    fun toggleCurrentDone()
    fun toggleById(itemId: String)
    fun setStatus(itemId: String, status: ItemStatus)

    fun jumpToSection(sectionIndex: Int)
}

class ChecklistPlayerImpl @Inject constructor() : ChecklistPlayer {
    private val _state = MutableStateFlow<PlaybackState?>(null)
    override val state: StateFlow<PlaybackState?> = _state

    override fun load(template: CheckListTemplateModel) {
        val flat = template.flattenForPlayback()
        val initial = flat.items.associate { ref ->
            val completed = ref.itemBlock.item.completed
            ref.itemBlock.item.id to if (completed) ItemStatus.DONE else ItemStatus.PENDING
        }
        _state.value = PlaybackState(flat = flat, cursor = 0, statuses = initial)
    }

    override fun reset() {
        _state.value = null
    }

    override fun next() {
        val s = _state.value ?: return
        if (s.total == 0) return
        if (s.cursor < s.total - 1) {
            _state.value = s.copy(cursor = s.cursor + 1)
        }
    }

    override fun prev() {
        val s = _state.value ?: return
        if (s.total == 0) return
        if (s.cursor > 0) {
            _state.value = s.copy(cursor = s.cursor - 1)
        }
    }

    override fun jumpTo(globalIndex: Int) {
        val s = _state.value ?: return
        if (s.total == 0) return
        val bounded = globalIndex.coerceIn(0, s.total - 1)
        if (bounded != s.cursor) {
            _state.value = s.copy(cursor = bounded)
        }
    }

    override fun toggleCurrentDone() {
        val s = _state.value ?: return
        if (s.total == 0) return
        val id = s.current.itemBlock.item.id
        val new = if (s.statuses[id] == ItemStatus.DONE) ItemStatus.PENDING else ItemStatus.DONE
        _state.value = s.copy(statuses = s.statuses + (id to new))
    }

    override fun toggleById(itemId: String) {
        val s = _state.value ?: return
        val new = if (s.statuses[itemId] == ItemStatus.DONE) ItemStatus.PENDING else ItemStatus.DONE
        _state.value = s.copy(statuses = s.statuses + (itemId to new))
    }

    override fun setStatus(itemId: String, status: ItemStatus) {
        val s = _state.value ?: return
        _state.value = s.copy(statuses = s.statuses + (itemId to status))
    }

    override fun jumpToSection(sectionIndex: Int) {
        val s = _state.value ?: return
        if (s.total == 0) return

        val starts = s.flat.sectionStartsAt
        if (sectionIndex !in starts.indices) return

        val start = starts[sectionIndex]
        val end = if (sectionIndex < starts.lastIndex) starts[sectionIndex + 1] else s.total

        if (start !in 0..max(0, s.total - 1)) return

        // Intentar ir al primer ítem NO DONE de esa sección; si no hay, ir al inicio.
        val firstPending = (start until end).firstOrNull { g ->
            val id = s.flat.items[g].itemBlock.item.id
            s.statuses[id] != ItemStatus.DONE
        }

        val target = firstPending ?: start
        _state.value = s.copy(cursor = target)
    }
}
