package com.sergiom.flycheck.di

import com.sergiom.flycheck.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEditorUseCases(): EditorUseCases {
        return EditorUseCases(
            addItem = AddItemUseCase(),
            addSubsection = AddSubsectionUseCase(),
            deleteItem = DeleteItemUseCase(),
            deleteSubsection = DeleteSubsectionUseCase(),
            deleteSection = DeleteSectionUseCase(), // ← ✅ Añadir esto
            updateItem = UpdateItemUseCase(),
            toggleItemCompletion = ToggleItemCompletionUseCase(),
            updateSectionTitle = UpdateSectionTitleUseCase(),
            updateSubsectionTitle = UpdateSubsectionTitleUseCase()
        )
    }
}