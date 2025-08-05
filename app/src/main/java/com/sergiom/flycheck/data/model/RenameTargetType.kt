package com.sergiom.flycheck.data.model

import kotlinx.serialization.Serializable


@Serializable
enum class RenameTargetType {
    SECTION,
    SUBSECTION
}