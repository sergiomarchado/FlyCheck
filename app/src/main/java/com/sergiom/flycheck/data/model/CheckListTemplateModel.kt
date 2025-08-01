package com.sergiom.flycheck.data.model

import java.util.UUID


data class CheckListTemplateModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val aircraftModel: String = "",
    val airline: String = "",
    val includeLogo: Boolean = false,
    val blocks: List<CheckListBlock> = emptyList()
)
