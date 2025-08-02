
package com.sergiom.flycheck.data.model

sealed class FlatBlock {
    abstract val sectionId: String

    data class SectionHeader(
        override val sectionId: String,
        val section: CheckListSection
    ) : FlatBlock()

    data class Item(
        override val sectionId: String,
        val itemBlock: CheckListBlock.ItemBlock
    ) : FlatBlock()

    data class Subsection(
        override val sectionId: String,
        val subsectionBlock: CheckListBlock.SubsectionBlock
    ) : FlatBlock()

    data class AddControls(
        override val sectionId: String
    ) : FlatBlock()
}


