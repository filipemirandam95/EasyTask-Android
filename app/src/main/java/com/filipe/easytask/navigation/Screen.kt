package com.filipe.easytask.navigation

/**
 * Sealed class para definir rotas tipadas, evitando hardcoded strings espalhadas pelo app.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Tasks : Screen("tasks")
    object Groups : Screen("groups")
    object Profile : Screen("profile")

    object TaskDetail : Screen("task/{taskId}") {
        fun createRoute(taskId: String) = "task/$taskId"
    }

    object GroupDetail : Screen("group/{groupId}") {
        fun createRoute(groupId: String) = "group/$groupId"
    }

    object CreateTask : Screen("create_task")
}