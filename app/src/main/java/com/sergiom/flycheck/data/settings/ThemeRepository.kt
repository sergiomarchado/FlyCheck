package com.sergiom.flycheck.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sergiom.flycheck.ui.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension a nivel de archivo (no dentro de una clase)
private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")
private val THEME_KEY = stringPreferencesKey("theme_mode")

@Singleton
class ThemeRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun observeMode(): Flow<ThemeMode> =
        context.themeDataStore.data.map { prefs ->
            when (prefs[THEME_KEY]) {
                ThemeMode.DARK.name -> ThemeMode.DARK
                ThemeMode.LIGHT.name -> ThemeMode.LIGHT
                else -> ThemeMode.SYSTEM
            }
        }

    suspend fun setMode(mode: ThemeMode) {
        context.themeDataStore.edit { it[THEME_KEY] = mode.name }
    }
}
