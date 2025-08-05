package com.sergiom.flycheck.ui.screens.b_custom.components.editor.utils

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.sergiom.flycheck.R
import com.sergiom.flycheck.ui.events.UiEvent
import com.sergiom.flycheck.presentation.viewmodel.TemplateEditorViewModel

/**
 * Composable responsable de observar los eventos de UI emitidos desde el ViewModel
 * y ejecutar efectos secundarios en consecuencia, como mostrar toasts.
 *
 * @param viewModel El ViewModel que emite los eventos de UI mediante un Flow.
 */
@Composable
fun ObserveUiEvents(viewModel: TemplateEditorViewModel) {
    // Obtiene el contexto actual del sistema (necesario para mostrar Toasts)
    val context = LocalContext.current

    // LaunchedEffect se ejecuta una única vez cuando se compone este Composable
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        context.getString(event.resId),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is UiEvent.ShareFile -> {
                    context.startActivity(
                        Intent.createChooser(
                            event.intent,
                            context.getString(R.string.share_file_chooser_title)
                        )
                    )
                }

                // Por si añades más eventos en el futuro
                else -> {}
            }
        }
    }

}

