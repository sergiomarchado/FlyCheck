// di/PlayerModule.kt
package com.sergiom.flycheck.di

import com.sergiom.flycheck.domain.player.ChecklistPlayer
import com.sergiom.flycheck.domain.player.ChecklistPlayerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {
    @Binds
    @Singleton
    abstract fun bindChecklistPlayer(impl: ChecklistPlayerImpl): ChecklistPlayer
}
