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
import com.filipe.easytask.ui.screens.tabs.GroupsScreen
import com.filipe.easytask.ui.screens.tabs.ProfileScreen
import com.filipe.easytask.ui.screens.tabs.TasksScreen

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

            // Telas Temporárias de Detalhes (mantidas da Etapa 2 para não quebrar a navegação)
            composable(
                route = Screen.TaskDetail.route,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")
                PlaceholderScreen("Detalhes da Tarefa\nID: $taskId") {
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Voltar")
                    }
                }
            }

            composable(
                route = Screen.GroupDetail.route,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")
                PlaceholderScreen("Detalhes do Grupo\nID: $groupId") {
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Voltar")
                    }
                }
            }

            composable(Screen.CreateTask.route) {
                PlaceholderScreen("Criar Tarefa") {
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Voltar")
                    }
                }
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