package com.sergiom.flycheck.navigation

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sergiom.flycheck.presentation.viewmodel.player.ChecklistDisplayerViewModel
import com.sergiom.flycheck.ui.screens.a_welcome.HomeScreenContainer
import com.sergiom.flycheck.ui.screens.a_welcome.SplashScreen
import com.sergiom.flycheck.ui.screens.b_editor.PreCheckListEditorScreen
import com.sergiom.flycheck.ui.screens.b_editor.TemplateEditorCheckListScreen
import com.sergiom.flycheck.ui.screens.c_displayer.ChecklistDisplayerScreen
import com.sergiom.flycheck.ui.screens.c_displayer.ChecklistManagerScreen
import com.sergiom.flycheck.ui.utils.JsonUtils
import com.sergiom.flycheck.data.models.CheckListTemplateModel
import com.sergiom.flycheck.presentation.viewmodel.manager.ChecklistManagerViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(navController = navController, startDestination = NavigationRoutes.Splash.route) {

        // Splash
        composable(NavigationRoutes.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(NavigationRoutes.Home.route) {
                        popUpTo(NavigationRoutes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(NavigationRoutes.Home.route) {
            HomeScreenContainer(navController = navController)
        }

        // Crear checklist custom
        composable(NavigationRoutes.CheckListCustom.route) {
            PreCheckListEditorScreen(
                navController = navController,
                onContinue = { name, model, airline, logo, sectionCount, logoUri ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("logoUri", logoUri)
                    navController.navigate(
                        NavigationRoutes.CheckListEditor.withArgs(
                            name, model, airline, logo, sectionCount
                        )
                    )
                }
            )
        }

        // Editor
        composable(
            route = NavigationRoutes.CheckListEditor.FULLROUTE
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val model = backStackEntry.arguments?.getString("model") ?: ""
            val airline = backStackEntry.arguments?.getString("airline") ?: ""
            val logo = backStackEntry.arguments?.getString("logo")?.toBooleanStrictOrNull() ?: false
            val sectionCount = backStackEntry.arguments?.getString("sectionCount")?.toIntOrNull() ?: 1
            val logoUri: Uri? = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Uri>("logoUri")

            TemplateEditorCheckListScreen(
                templateName = name,
                model = model,
                airline = airline,
                includeLogo = logo,
                sectionCount = sectionCount,
                navController = navController,
                logoUri = logoUri
            )
        }

        // Predefined (pendiente)
        composable(NavigationRoutes.CheckListPredefined.route) {
            Text("Predefinidas próximamente")
        }

        // Manager (gestor de checklists locales)
        composable(NavigationRoutes.ChecklistManager.route) {
            val vm: ChecklistManagerViewModel = hiltViewModel()
            val items by vm.checklists.collectAsState()

            ChecklistManagerScreen(
                items = items,
                onSelect = { info ->
                    vm.loadChecklist(info.id)?.let { template ->
                        val jsonStr = JsonUtils.json.encodeToString(
                            CheckListTemplateModel.serializer(), template
                        )
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("templateForPlaybackJson", jsonStr)
                        navController.navigate(NavigationRoutes.ChecklistDisplayer.FULLROUTE)
                    }
                },
                onDelete = { info -> vm.deleteChecklist(info.id) },
                onRename = { info, newName -> vm.renameChecklist(info.id, newName) }
            )
        }

        // Checklist Displayer
        composable(NavigationRoutes.ChecklistDisplayer.FULLROUTE) {
            val vm: ChecklistDisplayerViewModel = hiltViewModel()

            // Recupera el JSON dejado en el entry anterior (Home/Manager) ANTES de navegar
            val jsonStr: String? =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get("templateForPlaybackJson")

            LaunchedEffect(jsonStr) {
                if (!jsonStr.isNullOrBlank()) {
                    vm.initWithTemplateJson(jsonStr) // El VM decodifica y hace player.load
                    // Limpia la clave para evitar re-inicializaciones al volver atrás
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.remove<String>("templateForPlaybackJson")
                }
            }

            val state = vm.uiState.collectAsState()
            ChecklistDisplayerScreen(
                state = state.value,
                onPrev = vm::onPrev,
                onNext = vm::onNext,
                onToggle = vm::onToggle,
                onSelectSection = vm::onJumpToSection,
                onBack = { navController.popBackStack() }
            )
        }

        // Comunidad (placeholder)
        composable(NavigationRoutes.Community.route) {
            Text("Comunidad Próximamente")
        }
    }
}
