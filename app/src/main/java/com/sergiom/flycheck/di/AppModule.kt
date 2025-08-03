package com.sergiom.flycheck.di

import com.sergiom.flycheck.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Módulo de Dagger-Hilt encargado de proporcionar dependencias a nivel de aplicación.
// Aquí se definen los casos de uso del editor como dependencias inyectables.
@Module
@InstallIn(SingletonComponent::class)  // Este módulo vive mientras viva la aplicación.
object AppModule {

    // Proporciona una instancia de la clase contenedora EditorUseCases,
    // que agrupa todos los casos de uso relacionados con la edición de plantillas.
    @Provides
    @Singleton  // La misma instancia se compartirá en toda la app.
    fun provideEditorUseCases(): EditorUseCases {
        return EditorUseCases(
            // Cada uno de estos casos de uso encapsula una operación específica del editor.
            addItem = AddItemUseCase(),
            addSubsection = AddSubsectionUseCase(),
            deleteItem = DeleteItemUseCase(),
            deleteSubsection = DeleteSubsectionUseCase(),
            deleteSection = DeleteSectionUseCase(), // ← ✅ Añadir esto
            updateItem = UpdateItemUseCase(),
            toggleItemCompletion = ToggleItemCompletionUseCase(),
            updateSectionTitle = UpdateSectionTitleUseCase(),
            updateSubsectionTitle = UpdateSubsectionTitleUseCase(),
            addSectionUseCase = AddSectionUseCase()
        )
    }
}