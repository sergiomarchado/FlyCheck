package com.sergiom.flycheck.data.model

import kotlinx.serialization.Serializable


@Serializable
sealed class CheckListBlock {
    abstract val id: String

    @Serializable
    data class SectionBlock(val section: CheckListSection) : CheckListBlock() {
        override val id: String get() = section.id
    }

    @Serializable
    data class ItemBlock(val item: CheckListItemModel) : CheckListBlock() {
        override val id: String get() = item.id
    }

    @Serializable
    data class SubsectionBlock(val subsection: CheckListSection) : CheckListBlock() {
        override val id: String get() = subsection.id
    }
}
