package com.filipe.easytask.ui.screens.creategroup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filipe.easytask.data.MockRepository
import com.filipe.easytask.data.model.Group
import com.filipe.easytask.ui.components.AppButton
import com.filipe.easytask.ui.components.AppTextField
import com.filipe.easytask.ui.components.ColorPicker
import com.filipe.easytask.ui.theme.GroupColorBlue
import com.filipe.easytask.ui.theme.GroupColorGreen
import com.filipe.easytask.ui.theme.GroupColorPink
import com.filipe.easytask.ui.theme.GroupColorRed
import com.filipe.easytask.ui.theme.GroupColorTeal
import com.filipe.easytask.ui.theme.GroupColorYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onBack: () -> Unit,
    onGroupCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var emails by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("📁") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Mapa de cores predefinidas para os valores HEX esperados no modelo
    val colorMap = mapOf(
        GroupColorRed to "#FF6B6B",
        GroupColorTeal to "#4ECDC4",
        GroupColorBlue to "#45B7D1",
        GroupColorGreen to "#96CEB4",
        GroupColorYellow to "#FFEEAD",
        GroupColorPink to "#D4A5A5"
    )
    val presetColors = colorMap.keys.toList()
    var selectedColor by remember { mutableStateOf(presetColors.first()) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Grupo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()) // Permite scroll quando o teclado abrir
        ) {
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nome do Grupo (Obrigatório)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descrição (Opcional)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = emoji,
                onValueChange = { if (it.length <= 2) emoji = it },
                label = "Emoji (Ex: 💻, 📚)"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Cor de Destaque",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            ColorPicker(
                colors = presetColors,
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = emails,
                onValueChange = { emails = it },
                label = "Convidar membros (E-mails separados por vírgula)"
            )

            Spacer(modifier = Modifier.height(48.dp))

            AppButton(
                text = "Salvar Grupo",
                isLoading = isLoading,
                onClick = {
                    if (name.isBlank()) {
                        showErrorDialog = true
                    } else {
                        coroutineScope.launch {
                            isLoading = true
                            delay(800) // Simula rede

                            val currentUser = MockRepository.mockCurrentUser
                            val newGroupId = UUID.randomUUID().toString()

                            val newGroup = Group(
                                id = newGroupId,
                                name = name,
                                description = description.ifBlank { null },
                                emoji = emoji.ifBlank { "📁" },
                                color = colorMap[selectedColor] ?: "#45B7D1",
                                createdAt = "2024-01-10",
                                createdBy = currentUser.id
                            )

                            MockRepository.addGroup(newGroup)
                            MockRepository.addOwnerToGroup(newGroupId, currentUser.id, currentUser.email)

                            isLoading = false
                            onGroupCreated()
                        }
                    }
                }
            )
        }

        // Validação de nome em branco
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Atenção") },
                text = { Text("O nome do grupo é obrigatório.") },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("Entendi")
                    }
                }
            )
        }
    }
}