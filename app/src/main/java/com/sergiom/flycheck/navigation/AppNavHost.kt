package com.sergiom.flycheck.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sergiom.flycheck.ui.screens.CreateCheckListScreen
import com.sergiom.flycheck.ui.screens.HomeScreen
import com.sergiom.flycheck.ui.screens.SplashScreen


@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(navController = navController, startDestination = NavigationRoutes.Splash.route){
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

        composable(NavigationRoutes.Home.route){
            HomeScreen(
                onGoCustomCheckList = {
                    navController.navigate(NavigationRoutes.CheckListCustom.route)
                },
                onGoPredefinedCheckList = {navController.navigate(NavigationRoutes.CheckListPredefined.route)}
            )
        }

        composable(NavigationRoutes.CheckListCustom.route) {
            CreateCheckListScreen(
                onContinue = { name ->
                    navController.navigate(NavigationRoutes.CheckListEditor.route)
                }
            )
        }

        composable(NavigationRoutes.CheckListPredefined.route) {
            // Pendiente de implementar más adelante
        }
    }



}

