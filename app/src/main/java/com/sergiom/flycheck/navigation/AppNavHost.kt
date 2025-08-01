package com.sergiom.flycheck.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sergiom.flycheck.ui.screens.a_welcome.HomeScreen
import com.sergiom.flycheck.ui.screens.a_welcome.SplashScreen
import com.sergiom.flycheck.ui.screens.b_custom.PreCheckListEditorScreen
import com.sergiom.flycheck.ui.screens.b_custom.TemplateEditorCheckListScreen


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(navController = navController, startDestination = NavigationRoutes.Splash.route){

        // Splash
        composable(NavigationRoutes.Splash.route) {
            // Simulamos un splash screen corto con delay
            SplashScreen(
                onTimeout = {
                    navController.navigate(NavigationRoutes.Home.route){
                        popUpTo(NavigationRoutes.Splash.route) { inclusive = true  }
                    }
                }
            )
        }

        // HOME SCREEN TRAS SPLASCREEN
        composable(NavigationRoutes.Home.route){
            HomeScreen(
                onGoCustomCheckList = {
                    navController.navigate(NavigationRoutes.CheckListCustom.route)
                },
                onGoPredefinedCheckList = {navController.navigate(NavigationRoutes.CheckListPredefined.route)}
            )
        }

        // OPCIÓN DE CREAR CHECK LIST PERSONALIZADA O CUSTOM
        composable(NavigationRoutes.CheckListCustom.route) {
            PreCheckListEditorScreen(
                navController = navController,
                onContinue = { name, model, airline, logo, sectionCount ->
                    navController.navigate(
                        NavigationRoutes.CheckListEditor.withArgs(
                            name, model, airline, logo, sectionCount
                        )
                    )
                }
            )
        }

        /// Editor checklist con datos pasados por navegación
        composable(
            route = NavigationRoutes.CheckListEditor.FULLROUTE
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val model = backStackEntry.arguments?.getString("model") ?: ""
            val airline = backStackEntry.arguments?.getString("airline") ?: ""
            val logo = backStackEntry.arguments?.getString("logo")?.toBooleanStrictOrNull() ?: false
            val sectionCount = backStackEntry.arguments?.getString("sectionCount")?.toIntOrNull() ?: 1

            TemplateEditorCheckListScreen(
                templateName = name,
                model = model,
                airline = airline,
                includeLogo = logo,
                sectionCount = sectionCount,
                navController = navController
            )
        }

        composable(NavigationRoutes.CheckListPredefined.route) {
            // Pendiente de implementar más adelante
        }
    }
}

