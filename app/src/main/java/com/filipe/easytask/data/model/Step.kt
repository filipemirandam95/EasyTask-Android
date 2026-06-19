package com.filipe.easytask.data.model

data class Step(
    val id: String,
    val taskId: String,
    val title: String,
    val assignedTo: String?,
    val isCompleted: Boolean,
    val createdAt: String
)