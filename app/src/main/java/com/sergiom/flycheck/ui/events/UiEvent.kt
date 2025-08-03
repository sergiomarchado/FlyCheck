package com.sergiom.flycheck.ui.events

import androidx.annotation.StringRes

sealed class UiEvent {
    data class ShowToast(@StringRes val resId: Int) : UiEvent()
}

