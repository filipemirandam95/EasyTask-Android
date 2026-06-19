package com.filipe.easytask.data.model

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val groupId: String,
    val isCompleted: Boolean,
    val createdAt: String,
    val createdBy: String,
    val group: Group? = null,
    val imageUri: String? = null
)