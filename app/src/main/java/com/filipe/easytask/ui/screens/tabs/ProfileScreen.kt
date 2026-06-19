package com.filipe.easytask.ui.screens.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.filipe.easytask.ui.components.AppButton
import com.filipe.easytask.ui.components.ButtonVariant

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Perfil",
            modifier = Modifier.fillMaxSize(0.3f),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Usuário Teste",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "usuario@easytask.com",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        AppButton(
            text = "Sair da Conta",
            onClick = onLogout,
            variant = ButtonVariant.DANGER
        )
    }
}