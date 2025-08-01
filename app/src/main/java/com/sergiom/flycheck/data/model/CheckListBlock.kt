package com.sergiom.flycheck.data.model


sealed class CheckListBlock {
    data class SectionBlock(val section: CheckListSection) : CheckListBlock()
    data class ItemBlock(val item: CheckListItemModel) : CheckListBlock()
    data class SubsectionBlock(val subsection: CheckListSection) : CheckListBlock()
}
