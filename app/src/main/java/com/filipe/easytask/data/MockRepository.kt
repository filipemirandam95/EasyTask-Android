package com.filipe.easytask.data

import com.filipe.easytask.data.model.*

object MockRepository {

    val mockCurrentUser = User(
        id = "user_1",
        email = "joao@email.com",
        name = "João Silva"
    )

    private val mockGroups = mutableListOf(
        Group(id = "g1", name = "Eng. de Software", description = "Projeto ADS", emoji = "💻", color = "#45B7D1", createdAt = "2024-01-01", createdBy = "user_1"),
        Group(id = "g2", name = "TCC", description = "Monografia", emoji = "📚", color = "#4ECDC4", createdAt = "2024-01-02", createdBy = "user_2"),
        Group(id = "g3", name = "Estágio TI", description = "Sprint tarefas", emoji = "💼", color = "#96CEB4", createdAt = "2024-01-03", createdBy = "user_1")
    )

    private val mockTasks = mutableListOf(
        Task(id = "t1", title = "Desenvolver tela de Login", description = "UI completa + validação", groupId = "g1", isCompleted = false, createdAt = "2024-01-01", createdBy = "user_1"),
        Task(id = "t2", title = "Configurar Supabase", description = null, groupId = "g1", isCompleted = true, createdAt = "2024-01-02", createdBy = "user_1"),
        Task(id = "t3", title = "Pesquisar referências TCC", description = "Artigos ABNT", groupId = "g2", isCompleted = false, createdAt = "2024-01-03", createdBy = "user_2"),
        Task(id = "t4", title = "Revisar metodologia", description = null, groupId = "g2", isCompleted = false, createdAt = "2024-01-04", createdBy = "user_3"),
        Task(id = "t5", title = "Documentar endpoints da API", description = "Swagger/OpenAPI", groupId = "g3", isCompleted = true, createdAt = "2024-01-05", createdBy = "user_1")
    )

    private val mockSteps = mutableListOf(
        Step(id = "s1", taskId = "t1", title = "Criar layout da tela", assignedTo = null, isCompleted = true, createdAt = "2024-01-01"),
        Step(id = "s2", taskId = "t1", title = "Implementar validação campos", assignedTo = "user_2", isCompleted = false, createdAt = "2024-01-02"),
        Step(id = "s3", taskId = "t1", title = "Testes de integração", assignedTo = "user_1", isCompleted = false, createdAt = "2024-01-03"),
        Step(id = "s4", taskId = "t3", title = "Buscar artigos no Google Scholar", assignedTo = "user_2", isCompleted = true, createdAt = "2024-01-04"),
        Step(id = "s5", taskId = "t3", title = "Fichamento das referências", assignedTo = "user_3", isCompleted = false, createdAt = "2024-01-05")
    )

    private val mockGroupMembers = mutableMapOf(
        "g1" to mutableListOf(Member("user_1", "g1", "joao@email.com", "owner"), Member("user_2", "g1", "maria@email.com", "member")),
        "g2" to mutableListOf(Member("user_2", "g2", "maria@email.com", "owner"), Member("user_3", "g2", "pedro@email.com", "member")),
        "g3" to mutableListOf(Member("user_1", "g3", "joao@email.com", "owner"))
    )

    fun getGroupsForUser(userId: String): List<Group> {
        val userGroupIds = mockGroupMembers.filterValues { members ->
            members.any { it.userId == userId }
        }.keys
        return mockGroups.filter { it.id in userGroupIds }
    }

    fun getTasksForUser(userId: String): List<Task> {
        val userGroups = getGroupsForUser(userId)
        val groupIds = userGroups.map { it.id }
        return mockTasks.filter { it.groupId in groupIds }.map { task ->
            task.copy(group = mockGroups.find { it.id == task.groupId })
        }
    }

    fun getGroupById(id: String): Group? = mockGroups.find { it.id == id }

    fun getTaskById(id: String): Task? = mockTasks.find { it.id == id }

    fun getStepsForTask(taskId: String): List<Step> = mockSteps.filter { it.taskId == taskId }

    fun getMembersForGroup(groupId: String): List<Member> = mockGroupMembers[groupId] ?: emptyList()

    fun getTasksForGroup(groupId: String): List<Task> = mockTasks.filter { it.groupId == groupId }

    fun addGroup(group: Group) {
        mockGroups.add(group)
    }

    fun addTask(task: Task) {
        mockTasks.add(task)
    }

    fun addStep(step: Step) {
        mockSteps.add(step)
    }

    fun updateGroup(groupId: String, name: String, description: String?) {
        val index = mockGroups.indexOfFirst { it.id == groupId }
        if (index != -1) {
            mockGroups[index] = mockGroups[index].copy(name = name, description = description)
        }
    }

    fun updateTask(taskId: String, title: String, description: String?) {
        val index = mockTasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            mockTasks[index] = mockTasks[index].copy(title = title, description = description)
        }
    }

    fun updateTaskStatus(taskId: String, isCompleted: Boolean) {
        val index = mockTasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            mockTasks[index] = mockTasks[index].copy(isCompleted = isCompleted)
        }
    }

    fun updateStepStatus(stepId: String, isCompleted: Boolean) {
        val index = mockSteps.indexOfFirst { it.id == stepId }
        if (index != -1) {
            mockSteps[index] = mockSteps[index].copy(isCompleted = isCompleted)
        }
    }

    fun updateStepTitle(stepId: String, newTitle: String) {
        val index = mockSteps.indexOfFirst { it.id == stepId }
        if (index != -1) {
            mockSteps[index] = mockSteps[index].copy(title = newTitle)
        }
    }

    fun deleteGroup(groupId: String) {
        mockGroups.removeAll { it.id == groupId }
        mockTasks.removeAll { it.groupId == groupId }
        mockGroupMembers.remove(groupId)
    }

    fun deleteTask(taskId: String) {
        mockTasks.removeAll { it.id == taskId }
        mockSteps.removeAll { it.taskId == taskId }
    }

    fun removeMember(groupId: String, userId: String) {
        mockGroupMembers[groupId]?.removeAll { it.userId == userId }
    }

    // Função adicionada para registrar o criador como membro do grupo na hora da criação
    fun addOwnerToGroup(groupId: String, userId: String, email: String) {
        val newMember = Member(userId, groupId, email, "owner")
        if (mockGroupMembers.containsKey(groupId)) {
            mockGroupMembers[groupId]?.add(newMember)
        } else {
            mockGroupMembers[groupId] = mutableListOf(newMember)
        }
    }
}