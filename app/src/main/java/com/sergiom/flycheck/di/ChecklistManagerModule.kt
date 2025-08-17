package com.sergiom.flycheck.di

import android.content.Context
import com.sergiom.flycheck.data.local.ChecklistManagerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChecklistManagerModule {

    @Provides @Singleton
    fun provideChecklistManagerRepository(
        @ApplicationContext context: Context,
        json: Json,
        @IoDispatcher io: CoroutineDispatcher
    ): ChecklistManagerRepository {
        return ChecklistManagerRepository(context, json, io)
    }
}
