package com.sergiom.flycheck.data.model

import java.util.UUID

data class CheckListItemModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val action: String = "",
    val completed: Boolean = false,
    val backgroundColorHex: String = "#D3D3D3"
)

