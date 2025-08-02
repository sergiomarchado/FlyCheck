
package com.sergiom.flycheck.data.model

data class FlatBlockWithLocalIndex(
    val block: FlatBlock,
    val localIndex: Int? = null // Solo para FlatBlock.Item
)


