package com.varqulabs.dolarblue.history.presentation.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ButtonToScrollToTop(
    lazyListState: LazyListState,
    listIsNotEmpty: Boolean
) {
    val showScrollToTopButton by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }
    }
    val coroutineScope = rememberCoroutineScope()

    if (showScrollToTopButton && listIsNotEmpty) {
        SmallFloatingActionButton(
            containerColor = MaterialTheme.colorScheme.surface,
            shape = CircleShape,
            onClick = {
                coroutineScope.launch {
                    lazyListState.scrollToItem(0)
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Arrow up icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}