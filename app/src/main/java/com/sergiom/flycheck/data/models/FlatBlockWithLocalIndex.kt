
package com.sergiom.flycheck.data.models

import kotlinx.serialization.Serializable

@Serializable
data class FlatBlockWithLocalIndex(
    val block: FlatBlock,
    val localIndex: Int? = null // Solo para FlatBlock.Item
)


