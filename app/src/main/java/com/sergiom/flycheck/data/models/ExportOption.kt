package com.sergiom.flycheck.data.models

import kotlinx.serialization.Serializable

@Serializable
enum class ExportOption {
    Local,
    Community,
    File
}
