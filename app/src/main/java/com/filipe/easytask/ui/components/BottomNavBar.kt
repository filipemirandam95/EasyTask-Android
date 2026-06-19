package com.filipe.easytask.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.filipe.easytask.navigation.Screen

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String?) {
    val items = listOf(
        Triple(Screen.Tasks.route, "Tarefas", Icons.Default.CheckCircle),
        Triple(Screen.Groups.route, "Grupos", Icons.Default.Folder),
        Triple(Screen.Profile.route, "Perfil", Icons.Default.Person)
    )

    NavigationBar {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            // Evita criar uma pilha infinita de telas ao navegar entre as abas
                            popUpTo(Screen.Tasks.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}