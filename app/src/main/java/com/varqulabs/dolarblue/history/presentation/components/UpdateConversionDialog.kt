package com.varqulabs.dolarblue.history.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.varqulabs.dolarblue.history.presentation.HistoryState
import com.varqulabs.dolarblue.core.presentation.generics.buttons.Button5

@Composable
fun UpdateConversionDialog(
    state: HistoryState,
    onClickHideDialog: () -> Unit,
    onClickUpdateConversion: (String) -> Unit
) {
    var newConversionName by remember {
        mutableStateOf("")
    }
    val borderWidth = if(!isSystemInDarkTheme()) 2.dp else 0.dp
    val borderColor = if(!isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else Color.Transparent
    
    Dialog(onDismissRequest = { if (!state.isLoading) onClickHideDialog() }) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CardDefaults.elevatedShape
                ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ACTUALIZAR NOMBRE",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                }

                TextField(
                    value = newConversionName,
                    onValueChange = {
                        newConversionName = it
                    }
                )

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Button5(
                        text = "Cancelar",
                        enabled = !state.isLoading
                    ) {
                        onClickHideDialog()
                    }

                    Button5(
                        text = "Guardar",
                        enabled = newConversionName.isNotEmpty() && !state.isLoading
                    ) {
                        onClickUpdateConversion(newConversionName)
                    }
                }

                if (state.isLoading) CircularProgressIndicator()
            }
        }
    }
}