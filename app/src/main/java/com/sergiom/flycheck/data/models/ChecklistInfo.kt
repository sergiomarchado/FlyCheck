package com.sergiom.flycheck.data.models

data class ChecklistInfo(
    val id: String,          // filename sin extensión
    val name: String,
    val model: String,
    val airline: String,
    val lastModified: Long
)
