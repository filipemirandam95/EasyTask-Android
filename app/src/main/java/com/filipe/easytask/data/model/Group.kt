package com.filipe.easytask.data.model

data class Group(
    val id: String,
    val name: String,
    val description: String?,
    val emoji: String?,
    val color: String?,
    val createdAt: String,
    val createdBy: String
)