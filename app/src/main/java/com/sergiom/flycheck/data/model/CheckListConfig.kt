package com.sergiom.flycheck.data.model


data class CheckListConfig(
    val name: String = "",
    val modelAircraft: String = "",
    val airline: String = "",
    val includeLogo: Boolean = false,
    val sections: MutableList<CheckListSection> = mutableListOf()
)

