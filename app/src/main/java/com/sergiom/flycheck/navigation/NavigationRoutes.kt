package com.sergiom.flycheck.navigation

// Clase sellada que define todas las rutas de navegación de la aplicación.
sealed class NavigationRoutes(val route: String) {

    object Splash : NavigationRoutes("splash")
    object Home : NavigationRoutes("home")

    object CheckListCustom : NavigationRoutes("checklist_custom")
    object CheckListPredefined : NavigationRoutes("checklist_predefined") // Sin implementar

    object CheckListEditor : NavigationRoutes("checklist_editor") {

        // Genera una ruta completa con argumentos
        // para navegar al editor de checklist con parámetros predefinidos
        fun withArgs(
            name: String,
            model: String,
            airline: String,
            logo: Boolean,
            sectionCount: Int,
        ): String {
            // Los parámetros se inyectan directamente en la URI de la ruta
            return "checklist_editor/$name/$model/$airline/$logo/$sectionCount"
        }

        // Ruta con argumentos dinámicos, usada para declarar la navegación con NavController
        const val FULLROUTE = "checklist_editor/{name}/{model}/{airline}/{logo}/{sectionCount}"
    }

    // Displayer: sin args ( pasaremos el template de momento por SavedStateHandle).
    object ChecklistDisplayer : NavigationRoutes("checklist_displayer") {
        const val FULLROUTE = "checklist_displayer"
    }

    // Comunidad (placeholder)
    object Community : NavigationRoutes("community")

    // Manager de checklists locales
    object ChecklistManager: NavigationRoutes("manager")
}
