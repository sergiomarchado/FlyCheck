package com.sergiom.flycheck.navigation

sealed class NavigationRoutes (val route: String) {
    object Splash: NavigationRoutes("splash")
    object Home: NavigationRoutes("home")
    object CheckListCustom: NavigationRoutes("checklist_custom")
    object CheckListPredefined: NavigationRoutes("checklist_predefined")
    object CheckListEditor: NavigationRoutes("checklist_editor")
}