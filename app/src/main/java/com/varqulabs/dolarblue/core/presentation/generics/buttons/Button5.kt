package com.varqulabs.dolarblue.core.presentation.generics.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.varqulabs.dolarblue.core.presentation.utils.modifier.clickableSingleWithOutRipple

@Composable
fun Button5(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val borderColor = if (enabled) Color.Transparent else MaterialTheme.colorScheme.outline
    val borderWidth = if (enabled) 0.dp else 1.dp
    val backgroundColor = if (enabled) MaterialTheme.colorScheme.primary else Color.White
    val textColor =
        if (enabled) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .width(133.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickableSingleWithOutRipple {
                if (enabled) onClick()
            }
            .padding(
                vertical = 8.dp,
                horizontal = 5.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = textColor
        )
    }
    /*Button(
        modifier = Modifier
            .width(133.dp)
            .defaultMinSize(

            ),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            disabledContainerColor = Color.White,
            disabledContentColor = MaterialTheme.colorScheme.outline
            ),
        border = BorderStroke(width = borderWidth, color = borderColor),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = { onClick() }) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }*/
}