package com.filipe.easytask.ui.screens.createtask

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filipe.easytask.data.MockRepository
import com.filipe.easytask.data.model.Task
import com.filipe.easytask.ui.components.AppButton
import com.filipe.easytask.ui.components.AppTextField
import com.filipe.easytask.ui.components.GroupPill
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    preselectedGroupId: String?,
    onBack: () -> Unit,
    onTaskCreated: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Busca os grupos para o LazyRow de seleção
    val currentUser = MockRepository.mockCurrentUser
    val groups = remember { MockRepository.getGroupsForUser(currentUser.id) }

    // Inicializa com o grupo vindo da rota (se existir)
    var selectedGroupId by remember { mutableStateOf(preselectedGroupId) }

    var isLoading by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Tarefa") },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = "Título da Tarefa (Obrigatório)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descrição (Opcional)"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Selecione o Grupo",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (groups.isEmpty()) {
                Text(
                    text = "Você não possui grupos. Crie um grupo primeiro.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(groups, key = { it.id }) { group ->
                        GroupPill(
                            group = group,
                            isSelected = group.id == selectedGroupId,
                            onClick = { selectedGroupId = group.id }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Box clicável mockando a seleção de imagens
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Funcionalidade disponível em breve")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Câmera",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Adicionar Foto da Galeria",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            AppButton(
                text = "Salvar Tarefa",
                isLoading = isLoading,
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "O título da tarefa é obrigatório."
                        showErrorDialog = true
                    } else if (selectedGroupId == null) {
                        errorMessage = "Selecione um grupo para a tarefa."
                        showErrorDialog = true
                    } else {
                        coroutineScope.launch {
                            isLoading = true
                            delay(800) // Simulação

                            val newTask = Task(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                description = description.ifBlank { null },
                                groupId = selectedGroupId!!,
                                isCompleted = false,
                                createdAt = "2024-01-20", // Data fictícia fixa para o mock
                                createdBy = currentUser.id
                            )

                            MockRepository.addTask(newTask)

                            snackbarHostState.showSnackbar("Tarefa criada com sucesso! 📋")
                            delay(1000.milliseconds) // Deixa o usuário ler o snackbar antes de popBackStack

                            isLoading = false
                            onTaskCreated()
                        }
                    }
                }
            )
        }

        // Dialog de validação
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Atenção") },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("Entendi")
                    }
                }
            )
        }
    }
}