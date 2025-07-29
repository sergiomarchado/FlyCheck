package com.sergiom.flycheck.data.model

/**
 * Modelo de un ítem de checklist.
 *
 * Cada ítem representa una acción o verificación que debe realizarse
 * dentro de una checklist de una fase del vuelo (por ejemplo: Pre-Flight, Taxi, Landing...).
 *
 * Este modelo es inmutable (por ser data class), lo que garantiza que solo se puede modificar
 * creando una copia con valores nuevos (por ejemplo, al marcar como completado).
 */
data class CheckListItem(
    val id: Int,                     // Identificador único del ítem
    val text: String,              // Texto que describe la acción a realizar
    val completed: Boolean = false // Estado que indica si ya se ha completado
)

