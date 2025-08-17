package com.sergiom.flycheck.domain.player

import com.sergiom.flycheck.data.models.CheckListTemplateModel
import kotlinx.coroutines.flow.StateFlow

enum class ItemStatus { PENDING, DONE, SKIPPED }

data class PlaybackState(
    val flat: FlatPlayback,
    val cursor: Int = 0,
    val statuses: Map<String, ItemStatus> = emptyMap(), // key = itemId
    val paused: Boolean = false
) {
    val total: Int get() = flat.items.size
    val current: ItemRef get() = flat.items[cursor]  // ojo: acceder si total > 0
    val progress: Float get() = if (total == 0) 0f else (cursor + 1f) / total
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
