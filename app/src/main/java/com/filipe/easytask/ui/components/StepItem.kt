package com.filipe.easytask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.filipe.easytask.data.model.Step

@Composable
fun StepItem(
    step: Step,
    assigneeEmail: String?,
    isOwner: Boolean,
    isEditing: Boolean,
    editTitle: String,
    onEditTitleChange: (String) -> Unit,
    onToggleStatus: () -> Unit,
    onStartEdit: () -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (isEditing) {
            // Modo de Edição Inline
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AppTextField(
                        value = editTitle,
                        onValueChange = onEditTitleChange,
                        label = "Editar Etapa"
                    )
                }
                IconButton(onClick = onSaveEdit) {
                    Icon(Icons.Default.Check, contentDescription = "Salvar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onCancelEdit) {
                    Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = MaterialTheme.colorScheme.error)
                }
            }
        } else {
            // Modo de Visualização Normal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = step.isCompleted,
                        onCheckedChange = { onToggleStatus() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = if (step.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                        if (assigneeEmail != null) {
                            Text(
                                text = "Responsável: $assigneeEmail",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (isOwner) {
                    IconButton(onClick = onStartEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Etapa", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// Helper composable Box que usamos acima (apenas para estruturar melhor o Modifier.weight)
@Composable
private fun Box(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box(modifier = modifier) { content() }
}