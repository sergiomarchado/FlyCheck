package com.sergiom.flycheck.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CheckListSection(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "",
    val blocks: List<CheckListBlock> = mutableListOf()
)