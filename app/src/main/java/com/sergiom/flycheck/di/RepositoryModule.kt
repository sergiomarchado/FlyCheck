package com.sergiom.flycheck.di

import com.sergiom.flycheck.data.repository.FileChecklistRepository
import com.sergiom.flycheck.domain.repository.ChecklistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindChecklistRepository(
        impl: FileChecklistRepository
    ): ChecklistRepository
}
