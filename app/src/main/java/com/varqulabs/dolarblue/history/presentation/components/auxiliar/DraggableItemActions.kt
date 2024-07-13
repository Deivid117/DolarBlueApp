package com.varqulabs.dolarblue.history.presentation.components.auxiliar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.varqulabs.dolarblue.core.presentation.utils.modifier.clickableSingle

@Composable
fun EditAction(
    modifier: Modifier,
    onEditAction: () -> Unit
) {
    val iconEdit = EditIconPositive

    Action(
        modifier = modifier,
        color = Color(0xFF106B13),
        icon = iconEdit,
        nameAction = "Editar",
        onClickAction = { onEditAction() }
    )
}

@Composable
fun DeleteAction(
    modifier: Modifier,
    onDeleteAction: () -> Unit
) {
    Action(
        modifier = modifier,
        color = Color(0xFFB10C0C),
        icon = Icons.Outlined.Delete,
        nameAction = "Eliminar",
        onClickAction = { onDeleteAction() }
    )
}

@Composable
private fun Action(
    modifier: Modifier,
    color: Color,
    icon: ImageVector,
    nameAction: String,
    onClickAction: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(5.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color, CircleShape)
                    .clickableSingle { onClickAction() }
                    .padding(10.dp)
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                text = nameAction,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.inverseSurface
            )
        }
    }
}