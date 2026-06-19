package com.filipe.easytask.ui.screens.taskdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filipe.easytask.data.MockRepository
import com.filipe.easytask.ui.components.AppButton
import com.filipe.easytask.ui.components.AppTextField
import com.filipe.easytask.ui.components.ButtonVariant
import com.filipe.easytask.ui.components.StatusBadge
import com.filipe.easytask.ui.components.StepItem
import com.filipe.easytask.data.model.Step
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    onBack: () -> Unit
) {
    val currentUser = MockRepository.mockCurrentUser

    // Estados Locais
    var task by remember { mutableStateOf(MockRepository.getTaskById(taskId)) }
    var steps by remember { mutableStateOf(MockRepository.getStepsForTask(taskId)) }
    val members = remember { MockRepository.getMembersForGroup(task?.groupId ?: "") }

    // Estados para edição da Tarefa
    var isEditingTask by remember { mutableStateOf(false) }
    var editTaskTitle by remember { mutableStateOf(task?.title ?: "") }
    var editTaskDescription by remember { mutableStateOf(task?.description ?: "") }

    // Estados para gerenciamento de Steps (Etapas)
    var editingStepId by remember { mutableStateOf<String?>(null) }
    var editStepTitle by remember { mutableStateOf("") }

    var newStepTitle by remember { mutableStateOf("") }
    var selectedAssignee by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val isOwner = task?.createdBy == currentUser.id

    if (task == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tarefa não encontrada")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Tarefa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(onClick = { isEditingTask = !isEditingTask }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Tarefa")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir Tarefa")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                AppButton(
                    text = if (task!!.isCompleted) "Reabrir Tarefa" else "Concluir Tarefa",
                    variant = if (task!!.isCompleted) ButtonVariant.DANGER else ButtonVariant.PRIMARY,
                    onClick = {
                        MockRepository.updateTaskStatus(taskId, !task!!.isCompleted)
                        task = MockRepository.getTaskById(taskId)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Seção 1: Cabeçalho da Tarefa (Edição Inline ou Leitura)
            item {
                if (isEditingTask) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            AppTextField(
                                value = editTaskTitle,
                                onValueChange = { editTaskTitle = it },
                                label = "Título da Tarefa"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AppTextField(
                                value = editTaskDescription,
                                onValueChange = { editTaskDescription = it },
                                label = "Descrição"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(onClick = {
                                    isEditingTask = false
                                    editTaskTitle = task?.title ?: ""
                                    editTaskDescription = task?.description ?: ""
                                }) {
                                    Text("Cancelar")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    MockRepository.updateTask(taskId, editTaskTitle, editTaskDescription)
                                    task = MockRepository.getTaskById(taskId)
                                    isEditingTask = false
                                }) {
                                    Text("Salvar")
                                }
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = task!!.title,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.weight(1f)
                            )
                            StatusBadge(isCompleted = task!!.isCompleted)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        task!!.description?.let { desc ->
                            Text(text = desc, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            // Seção 2: Nova Etapa (Step)
            item {
                Text(
                    text = "Etapas (Steps)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (isOwner) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            AppTextField(
                                value = newStepTitle,
                                onValueChange = { newStepTitle = it },
                                label = "Nova Etapa..."
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Atribuir a:", style = MaterialTheme.typography.labelMedium)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(members) { member ->
                                    val displayName = member.userEmail.substringBefore("@")
                                    FilterChip(
                                        selected = selectedAssignee == member.userId,
                                        onClick = { selectedAssignee = member.userId },
                                        label = { Text(displayName) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (newStepTitle.isNotBlank()) {
                                        val newStep = Step(
                                            id = UUID.randomUUID().toString(),
                                            taskId = taskId,
                                            title = newStepTitle,
                                            assignedTo = selectedAssignee,
                                            isCompleted = false,
                                            createdAt = "2024-01-20"
                                        )
                                        MockRepository.addStep(newStep)
                                        steps = MockRepository.getStepsForTask(taskId) // recarrega
                                        newStepTitle = ""
                                        selectedAssignee = null
                                    }
                                },
                                modifier = Modifier.align(Alignment.End),
                                enabled = newStepTitle.isNotBlank()
                            ) {
                                Text("Adicionar Etapa")
                            }
                        }
                    }
                }
            }

            // Seção 3: Lista de Etapas
            items(steps, key = { it.id }) { step ->
                // O `key` acima é crucial para o Compose não perder o foco do TextField ao editar steps!
                val assigneeEmail = members.find { it.userId == step.assignedTo }?.userEmail

                StepItem(
                    step = step,
                    assigneeEmail = assigneeEmail,
                    isOwner = isOwner,
                    isEditing = editingStepId == step.id,
                    editTitle = editStepTitle,
                    onEditTitleChange = { editStepTitle = it },
                    onStartEdit = {
                        editingStepId = step.id
                        editStepTitle = step.title
                    },
                    onSaveEdit = {
                        MockRepository.updateStepTitle(step.id, editStepTitle)
                        steps = MockRepository.getStepsForTask(taskId)
                        editingStepId = null
                    },
                    onCancelEdit = {
                        editingStepId = null
                    },
                    onToggleStatus = {
                        MockRepository.updateStepStatus(step.id, !step.isCompleted)
                        steps = MockRepository.getStepsForTask(taskId)
                    }
                )
            }
        }
    }

    // Modal de Confirmação para Exclusão da Tarefa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir Tarefa") },
            text = { Text("Deseja realmente excluir esta tarefa permanentemente?") },
            confirmButton = {
                Button(
                    onClick = {
                        MockRepository.deleteTask(taskId)
                        showDeleteDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}