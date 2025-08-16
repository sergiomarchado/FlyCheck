package com.sergiom.flycheck.domain.player

import com.sergiom.flycheck.data.models.CheckListTemplateModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class ItemStatus { PENDING, DONE }

data class PlaybackState(
    val flat: FlatPlayback,
    val cursor: Int = 0,
    val statuses: Map<String, ItemStatus> = emptyMap(), // key = itemId
    val paused: Boolean = false
) {
    val total: Int get() = flat.items.size
    val current: ItemRef get() = flat.items[cursor]
    val progress: Float get() = if (total == 0) 0f else (cursor + 1f) / total
}

interface ChecklistPlayer {
    val state: StateFlow<PlaybackState?>
    fun load(template: CheckListTemplateModel)
    fun next()
    fun prev()
    fun toggleCurrentDone()
    fun jumpToSection(sectionIndex: Int)
    fun setStatus(itemId: String, status: ItemStatus)
}

class ChecklistPlayerImpl @Inject constructor() : ChecklistPlayer {

    private val _state = MutableStateFlow<PlaybackState?>(null)
    override val state: StateFlow<PlaybackState?> = _state

    override fun load(template: CheckListTemplateModel) {
        val flat = template.flattenForPlayback()
        val initialStatuses = flat.items.associate { ref ->
            val completed = ref.itemBlock.item.completed
            ref.itemBlock.item.id to if (completed) ItemStatus.DONE else ItemStatus.PENDING
        }
        _state.value = PlaybackState(flat = flat, cursor = 0, statuses = initialStatuses)
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

    override fun toggleCurrentDone() {
        val s = _state.value ?: return
        if (s.total == 0) return
        val id = s.current.itemBlock.item.id
        val newStatus = when (s.statuses[id]) {
            ItemStatus.DONE -> ItemStatus.PENDING
            else -> ItemStatus.DONE
        }
        _state.value = s.copy(statuses = s.statuses + (id to newStatus))
    }

    override fun setStatus(itemId: String, status: ItemStatus) {
        val s = _state.value ?: return
        if (s.total == 0) return
        _state.value = s.copy(statuses = s.statuses + (itemId to status))
    }

    override fun jumpToSection(sectionIndex: Int) {
        val s = _state.value ?: return
        if (s.total == 0) return
        val start = s.flat.sectionStartsAt.getOrNull(sectionIndex) ?: return
        _state.value = s.copy(cursor = start)
    }
}
