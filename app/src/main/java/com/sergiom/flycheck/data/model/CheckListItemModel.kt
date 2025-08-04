package com.sergiom.flycheck.data.model

import java.util.UUID

data class CheckListItemModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val action: String = "",
    val completed: Boolean = false,
    val backgroundColorHex: String = "#D3D3D3",
    val infoTitle: String? = null,
    val infoBody: String? = null,
    val imageUri: String? = null,
    val imageTitle: String? = null,
    val imageDescription: String? = null
)

