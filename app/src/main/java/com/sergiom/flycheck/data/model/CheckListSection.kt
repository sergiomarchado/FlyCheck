package com.sergiom.flycheck.data.model

import java.util.UUID


data class CheckListSection(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "",
    val items: List<CheckListItemModel> = emptyList()
)