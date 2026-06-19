package com.filipe.easytask.ui.screens.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.filipe.easytask.ui.components.AppButton

@Composable
fun TasksScreen(
    onNavigateToTaskDetail: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Minhas Tarefas",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "A lista real de tarefas virá aqui nas próximas etapas.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Botão temporário para validar a navegação para os detalhes
        AppButton(
            text = "Ver Detalhes da Tarefa (Mock)",
            onClick = { onNavigateToTaskDetail("T-100") }
        )
    }
}