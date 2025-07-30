package com.sergiom.flycheck.navigation

sealed class NavigationRoutes (val route: String) {
    object Splash: NavigationRoutes("splash")
    object Home: NavigationRoutes("home")
    object CheckListCustom: NavigationRoutes("checklist_custom")
    object CheckListPredefined: NavigationRoutes("checklist_predefined") // Sin implementar
    object CheckListEditor: NavigationRoutes("checklist_editor"){
        fun withArgs(
            name: String,
            model: String,
            airline: String,
            logo: Boolean,
            sectionCount: Int,
        ): String{
            return "checklist_editor/$name/$model/$airline/$logo/$sectionCount"
        }

        val fullRoute = "checklist_editor/{name}/{model}/{airline}/{logo}/{sectionCount}"
    }
}