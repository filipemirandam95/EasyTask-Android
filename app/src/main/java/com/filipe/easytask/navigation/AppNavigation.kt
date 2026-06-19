package com.filipe.easytask.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.filipe.easytask.ui.screens.auth.LoginScreen
import com.filipe.easytask.ui.screens.auth.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Limpa a pilha de navegação para que o usuário não volte pro login ao clicar em 'voltar'
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
                    // Se o cadastro for sucesso, vai direto pra Home (Tasks)
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
            PlaceholderScreen("Tasks Screen") {
                Button(onClick = { navController.navigate(Screen.Groups.route) }) {
                    Text("Ir para Groups")
                }
                Button(onClick = { navController.navigate(Screen.Profile.route) }) {
                    Text("Ir para Profile")
                }
                Button(onClick = { navController.navigate(Screen.TaskDetail.createRoute("T-100")) }) {
                    Text("Testar Task T-100")
                }
                Button(onClick = {
                    // Simulando logout limpando a pilha de navegação
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }) {
                    Text("Sair")
                }
            }
        }

        composable(Screen.Groups.route) {
            PlaceholderScreen("Groups Screen") {
                Button(onClick = { navController.navigate(Screen.GroupDetail.createRoute("G-999")) }) {
                    Text("Testar Group G-999")
                }
                Button(onClick = { navController.popBackStack() }) {
                    Text("Voltar")
                }
            }
        }

        composable(Screen.Profile.route) {
            PlaceholderScreen("Profile Screen") {
                Button(onClick = { navController.popBackStack() }) {
                    Text("Voltar")
                }
            }
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            PlaceholderScreen("Task Detail Screen\nID: $taskId") {
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
            PlaceholderScreen("Group Detail Screen\nID: $groupId") {
                Button(onClick = { navController.popBackStack() }) {
                    Text("Voltar")
                }
            }
        }
    }
}

/**
 * Componente temporário auxiliar para renderizar as telas visualmente limpas e centralizadas.
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