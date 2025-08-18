package com.sergiom.flycheck.data.models

import androidx.compose.runtime.Immutable
import com.sergiom.flycheck.domain.player.ItemStatus

@Immutable
data class DisplayerUiState(
    val title: String = "",
    val model: String = "",
    val airline: String = "",

    // Sección actual
    val sectionTitle: String = "",
    val sectionDone: Int = 0,
    val sectionTotal: Int = 0,

    // Ítem actual
    val subsectionPath: String = "",
    val itemTitle: String = "",
    val itemAction: String = "",
    val itemId: String = "",
    val itemStatus: ItemStatus = ItemStatus.PENDING,
    val progress: Float = 0f,     // global
    val total: Int = 0,
    val index: Int = 0,           // 1-based
    val emphasisColorHex: String = "#D3D3D3",
    val infoTitle: String? = null,
    val infoBody: String? = null,
    val imageUri: String? = null,
    val imageTitle: String? = null,
    val imageDescription: String? = null,

    // Navegación por secciones
    val sectionTitles: List<String> = emptyList(),
    val currentSectionIndex: Int = 0,
    val sectionSummaries: List<SectionSummary> = emptyList(),

    // Subsecciones de la sección actual
    val currentSectionSubsections: List<SubsectionSummary> = emptyList(),
    val currentSubsection: SubsectionSummary? = null
) {
    val sectionProgress: Float
        get() = if (sectionTotal <= 0) 0f else sectionDone.coerceIn(0, sectionTotal).toFloat() / sectionTotal
}