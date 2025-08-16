// ui/screens/a_welcome/HomeScreenContainer.kt
package com.sergiom.flycheck.ui.screens.a_welcome

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.domain.usecase.ImportTemplateFromUriUseCase
import com.sergiom.flycheck.navigation.NavigationRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private val MIME_TYPES = arrayOf(
    "application/json",
    "application/zip",
    "application/octet-stream" // algunos gestores marcan ZIP como octet-stream
)

@Composable
fun HomeScreenContainer(
    navController: NavHostController,
    vm: HomeVM = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    // Selector de archivos (JSON/ZIP) para "Abrir archivo (.json / .zip)"
    val openFile = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            // (Opcional) persistir permiso de lectura
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) { /* puede no ser necesario en algunos OEM */ }

            scope.launch {
                vm.importFromUri(context, uri)?.let { template ->
                    val jsonStr = com.sergiom.flycheck.ui.utils.JsonUtils.json.encodeToString(
                        CheckListTemplateModel.serializer(),
                        template
                    )
                    // Guardamos el JSON en el entry actual antes de navegar
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("templateForPlaybackJson", jsonStr)
                    navController.navigate(NavigationRoutes.ChecklistDisplayer.FULLROUTE)
                }
            }
        }
    }

    HomeScreen(
        onGoCustomCheckList = {
            navController.navigate(NavigationRoutes.CheckListCustom.route)
        },
        onOpenFromDevice = {
            openFile.launch(MIME_TYPES)
        },
        onOpenFromLocal = {
            // 👉 ahora abrimos el gestor de checklists locales
            navController.navigate(NavigationRoutes.ChecklistManager.route)
        },
        onOpenCommunity = {
            navController.navigate(NavigationRoutes.Community.route)
        }
    )
}

@HiltViewModel
class HomeVM @Inject constructor(
    private val importFromUriUseCase: ImportTemplateFromUriUseCase,
) : androidx.lifecycle.ViewModel() {

    fun importFromUri(context: Context, uri: Uri): CheckListTemplateModel? =
        importFromUriUseCase(context, uri).getOrNull()

}
