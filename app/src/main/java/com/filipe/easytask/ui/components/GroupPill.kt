package com.filipe.easytask.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.filipe.easytask.data.model.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPill(
    group: Group,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Conversão segura da String HEX (ex: "#45B7D1") vinda do mock para a cor do Compose
    val groupColor = try {
        Color(android.graphics.Color.parseColor(group.color ?: "#2F95DC"))
    } catch (e: Exception) {
        Color(0xFF2F95DC)
    }

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(text = "${group.emoji ?: ""} ${group.name}")
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = groupColor.copy(alpha = 0.2f),
            selectedLabelColor = groupColor.copy(alpha = 1.0f)
        )
    )
}