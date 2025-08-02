package com.sergiom.flycheck.data.model


sealed class CheckListBlock {
    abstract val id: String
    data class SectionBlock(val section: CheckListSection) : CheckListBlock() {
        override val id: String get() = section.id
    }

    data class ItemBlock(val item: CheckListItemModel) : CheckListBlock() {
        override val id: String get() = item.id
    }

    data class SubsectionBlock(val subsection: CheckListSection) : CheckListBlock() {
        override val id: String get() = subsection.id
    }
}
