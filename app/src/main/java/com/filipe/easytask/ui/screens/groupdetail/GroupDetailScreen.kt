package com.filipe.easytask.ui.screens.groupdetail

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filipe.easytask.data.MockRepository
import com.filipe.easytask.data.model.Member
import com.filipe.easytask.ui.components.AppTextField
import com.filipe.easytask.ui.components.EasyTaskFab
import com.filipe.easytask.ui.components.MemberChip
import com.filipe.easytask.ui.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String,
    onBack: () -> Unit,
    onCreateTask: (String) -> Unit,
    onNavigateToTaskDetail: (String) -> Unit
) {
    val currentUser = MockRepository.mockCurrentUser

    // Estados Locais
    var group by remember { mutableStateOf(MockRepository.getGroupById(groupId)) }
    var tasks by remember { mutableStateOf(MockRepository.getTasksForGroup(groupId)) }
    var members by remember { mutableStateOf(MockRepository.getMembersForGroup(groupId)) }

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(group?.name ?: "") }
    var editDescription by remember { mutableStateOf(group?.description ?: "") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var memberToRemove by remember { mutableStateOf<Member?>(null) }

    val isOwner = group?.createdBy == currentUser.id

    // Fallback caso o grupo seja excluído e haja uma tentativa de recomposição
    if (group == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Grupo não encontrado")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group?.name ?: "Detalhes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(onClick = { isEditing = !isEditing }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Grupo")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir Grupo")
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
        floatingActionButton = {
            EasyTaskFab(onClick = { onCreateTask(groupId) })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Seção 1: Cabeçalho (Exibição da Descrição ou Formulário de Edição Inline)
            item {
                if (isEditing) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            AppTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = "Nome do Grupo"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AppTextField(
                                value = editDescription,
                                onValueChange = { editDescription = it },
                                label = "Descrição"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(onClick = {
                                    isEditing = false
                                    editName = group?.name ?: ""
                                    editDescription = group?.description ?: ""
                                }) {
                                    Text("Cancelar")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    MockRepository.updateGroup(groupId, editName, editDescription)
                                    group = MockRepository.getGroupById(groupId) // Atualiza estado local
                                    isEditing = false
                                }) {
                                    Text("Salvar")
                                }
                            }
                        }
                    }
                } else {
                    group?.description?.let { desc ->
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                    }
                }
            }

            // Seção 2: Membros
            item {
                Text(
                    text = "Membros (${members.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(modifier = Modifier.padding(bottom = 24.dp)) {
                    items(members, key = { it.userId }) { member ->
                        val isMemberOwner = member.role == "owner"
                        val canRemove = isOwner && !isMemberOwner && member.userId != currentUser.id

                        MemberChip(
                            member = member,
                            isOwner = isMemberOwner,
                            canRemove = canRemove,
                            onRemove = { memberToRemove = member }
                        )
                    }
                }
            }

            // Seção 3: Tarefas do Grupo
            item {
                Text(
                    text = "Tarefas do Grupo",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (tasks.isEmpty()) {
                    Text(
                        text = "Nenhuma tarefa vinculada a este grupo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(tasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onClick = { onNavigateToTaskDetail(task.id) }
                )
            }
        }
    }

    // Modal de confirmação para exclusão de grupo
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir Grupo") },
            text = { Text("Tem certeza que deseja excluir o grupo '${group?.name}'? Esta ação também removerá as tarefas dele para todos os membros.") },
            confirmButton = {
                Button(
                    onClick = {
                        MockRepository.deleteGroup(groupId)
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

    // Modal de confirmação para remoção de membro
    if (memberToRemove != null) {
        AlertDialog(
            onDismissRequest = { memberToRemove = null },
            title = { Text("Remover Membro") },
            text = { Text("Deseja remover ${memberToRemove?.userEmail} do grupo?") },
            confirmButton = {
                Button(
                    onClick = {
                        memberToRemove?.let {
                            MockRepository.removeMember(groupId, it.userId)
                            members = MockRepository.getMembersForGroup(groupId) // Atualiza estado local
                        }
                        memberToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remover")
                }
            },
            dismissButton = {
                TextButton(onClick = { memberToRemove = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}