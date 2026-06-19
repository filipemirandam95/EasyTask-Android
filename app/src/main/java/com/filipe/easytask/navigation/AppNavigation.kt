package com.filipe.easytask.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.filipe.easytask.ui.components.BottomNavBar
import com.filipe.easytask.ui.screens.auth.LoginScreen
import com.filipe.easytask.ui.screens.auth.RegisterScreen
import com.filipe.easytask.ui.screens.creategroup.CreateGroupScreen
import com.filipe.easytask.ui.screens.createtask.CreateTaskScreen
import com.filipe.easytask.ui.screens.groupdetail.GroupDetailScreen
import com.filipe.easytask.ui.screens.tabs.GroupsScreen
import com.filipe.easytask.ui.screens.tabs.ProfileScreen
import com.filipe.easytask.ui.screens.tabs.TasksScreen
import com.filipe.easytask.ui.screens.taskdetail.TaskDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Observa a rota atual para decidir se mostra a barra inferior
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTabScreen = currentRoute in listOf(
        Screen.Tasks.route,
        Screen.Groups.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (isTabScreen) {
                BottomNavBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(paddingValues) // Aplica o padding apenas quando a barra existir
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Tasks.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Tasks.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Tasks.route) {
                TasksScreen(
                    onNavigateToTaskDetail = { taskId ->
                        navController.navigate(Screen.TaskDetail.createRoute(taskId))
                    },
                    onCreateTask = {
                        navController.navigate(Screen.CreateTask.route)
                    }
                )
            }

            composable(Screen.Groups.route) {
                GroupsScreen(
                    onNavigateToGroupDetail = { groupId ->
                        navController.navigate(Screen.GroupDetail.createRoute(groupId))
                    },
                    onCreateGroup = {
                        navController.navigate(Screen.CreateGroup.route)
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true } // Limpa toda a pilha ao sair
                        }
                    }
                )
            }

            composable(
                route = Screen.TaskDetail.route,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable

                TaskDetailScreen(
                    taskId = taskId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.GroupDetail.route,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable

                GroupDetailScreen(
                    groupId = groupId,
                    onBack = { navController.popBackStack() },
                    onCreateTask = { id ->
                        navController.navigate(Screen.CreateTask.createRoute(id))
                    },
                    onNavigateToTaskDetail = { taskId ->
                        navController.navigate(Screen.TaskDetail.createRoute(taskId))
                    }
                )
            }

            composable(
                route = Screen.CreateTask.route,
                arguments = listOf(navArgument("groupId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")

                CreateTaskScreen(
                    preselectedGroupId = groupId,
                    onBack = { navController.popBackStack() },
                    onTaskCreated = {
                        // Volta para a tela anterior (pode ser a lista de tarefas ou os detalhes de grupo)
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.CreateGroup.route) {
                CreateGroupScreen(
                    onBack = { navController.popBackStack() },
                    onGroupCreated = {
                        // Volta para a listagem de grupos, forçando recomposição para carregar o novo dado
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Componente temporário mantido apenas para as telas de detalhes que ainda não foram implementadas.
 */
@Composable
private fun PlaceholderScreen(title: String, content: @Composable () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}