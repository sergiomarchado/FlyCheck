
package com.sergiom.flycheck.data.models

import kotlinx.serialization.Serializable

@Serializable
sealed class FlatBlock {
    abstract val sectionId: String

    @Serializable
    data class SectionHeader(
        override val sectionId: String,
        val section: CheckListSection
    ) : FlatBlock()

    @Serializable
    data class Item(
        override val sectionId: String,
        val itemBlock: CheckListBlock.ItemBlock
    ) : FlatBlock()

    @Serializable
    data class Subsection(
        override val sectionId: String,
        val subsectionBlock: CheckListBlock.SubsectionBlock
    ) : FlatBlock()

    @Serializable
    data class AddControls(
        override val sectionId: String
    ) : FlatBlock()
}


