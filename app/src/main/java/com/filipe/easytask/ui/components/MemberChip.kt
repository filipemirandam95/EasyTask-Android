package com.filipe.easytask.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filipe.easytask.data.model.Member

@Composable
fun MemberChip(
    member: Member,
    isOwner: Boolean,
    canRemove: Boolean,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pega apenas a parte antes do "@" para não ficar um texto gigante
    val displayName = member.userEmail.substringBefore("@")
    val roleText = if (isOwner) " (Dono)" else ""

    AssistChip(
        onClick = { },
        label = { Text("$displayName$roleText") },
        leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null)
        },
        trailingIcon = {
            if (canRemove) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Remover",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        modifier = modifier.padding(end = 8.dp)
    )
}