package com.sergiom.flycheck.ui.screens.b_custom.components.editor

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.sergiom.flycheck.ui.events.UiEvent
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel

@Composable
fun ObserveUiEvents(viewModel: TemplateEditorViewModel) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            if (event is UiEvent.ShowToast) {
                Toast.makeText(context, context.getString(event.resId), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

