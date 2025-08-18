package com.sergiom.flycheck.viewmodel.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergiom.flycheck.data.settings.ThemeRepository
import com.sergiom.flycheck.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val repo: ThemeRepository
) : ViewModel() {

    val mode: StateFlow<ThemeMode> =
        repo.observeMode().stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    fun set(mode: ThemeMode) {
        viewModelScope.launch { repo.setMode(mode) }
    }

    /** Rota: SYSTEM → DARK → LIGHT → SYSTEM */
    fun cycle() {
        val next = when (mode.value) {
            ThemeMode.SYSTEM -> ThemeMode.DARK
            ThemeMode.DARK   -> ThemeMode.LIGHT
            ThemeMode.LIGHT  -> ThemeMode.SYSTEM
        }
        set(next)
    }
}
