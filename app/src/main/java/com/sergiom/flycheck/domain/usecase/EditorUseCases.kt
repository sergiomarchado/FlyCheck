package com.sergiom.flycheck.domain.usecase

data class EditorUseCases(
    val addItem: AddItemUseCase,
    val addSubsection: AddSubsectionUseCase,
    val deleteItem: DeleteItemUseCase,
    val deleteSubsection: DeleteSubsectionUseCase,
    val updateItem: UpdateItemUseCase,
    val toggleItemCompletion: ToggleItemCompletionUseCase,
    val updateSectionTitle: UpdateSectionTitleUseCase,
    val deleteSection: DeleteSectionUseCase,
)
