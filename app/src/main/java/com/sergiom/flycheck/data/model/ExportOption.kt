package com.sergiom.flycheck.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class ExportOption {
    Local,
    Community,
    File
}
