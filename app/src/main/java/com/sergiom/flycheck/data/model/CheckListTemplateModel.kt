package com.sergiom.flycheck.data.model

import android.net.Uri
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class CheckListTemplateModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val aircraftModel: String = "",
    val airline: String = "",
    val includeLogo: Boolean = false,
    @Contextual val logoUri: Uri? = null,
    val blocks: List<CheckListBlock> = emptyList()
)
