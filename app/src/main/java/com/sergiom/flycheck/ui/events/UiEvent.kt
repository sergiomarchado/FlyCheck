package com.sergiom.flycheck.ui.events

import android.content.Intent
import androidx.annotation.StringRes
import java.io.File

sealed class UiEvent {
    data class ShowToast(@StringRes val resId: Int) : UiEvent()
    data class ExportSuccess(val file: File) : UiEvent()

    data class ExportLocalSuccess(val file: File) : UiEvent()

    data class ShareFile(val intent: Intent) : UiEvent()

}

