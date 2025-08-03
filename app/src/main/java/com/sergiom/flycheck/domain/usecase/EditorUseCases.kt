package com.sergiom.flycheck.domain.usecase

// Clase contenedora que agrupa todos los casos de uso relacionados con la edición de una checklist.
// Esta clase facilita la inyección de múltiples casos de uso como una sola dependencia.
data class EditorUseCases(
    val addItem: AddItemUseCase,
    val addSubsection: AddSubsectionUseCase,
    val deleteItem: DeleteItemUseCase,
    val deleteSubsection: DeleteSubsectionUseCase,
    val updateItem: UpdateItemUseCase,
    val toggleItemCompletion: ToggleItemCompletionUseCase,
    val updateSectionTitle: UpdateSectionTitleUseCase,
    val updateSubsectionTitle: UpdateSubsectionTitleUseCase,
    val deleteSection: DeleteSectionUseCase
)
