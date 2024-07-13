package com.varqulabs.dolarblue.history.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.varqulabs.dolarblue.history.domain.model.Conversion
import com.varqulabs.dolarblue.history.domain.model.ConversionsHistory
import com.varqulabs.dolarblue.history.presentation.HistoryEvent
import com.varqulabs.dolarblue.history.presentation.HistoryEvent.*
import com.varqulabs.dolarblue.history.presentation.HistoryState
import com.varqulabs.dolarblue.history.presentation.compareDates
import com.varqulabs.dolarblue.history.presentation.components.auxiliar.DragToManageConversion
import kotlinx.coroutines.delay

// TODO: que el padre haga todos los eventhandler y los hijos tengan lambdas
@Composable
fun ConversionHistoryContent(
    lazyListState: LazyListState,
    state: HistoryState,
    eventHandler: (HistoryEvent) -> Unit
) {
    if (state.conversionsHistory.isNotEmpty()) {
        ConversionHistoryLazyColumn(
            lazyListState = lazyListState,
            state = state,
            onMarkConversionAsFavorite = { conversion ->
                eventHandler(OnClickSetConversion(conversion))
                eventHandler(OnSetFavoriteConversion(!conversion.isFavorite))
            },
            onDeleteConversion = { conversion ->
                eventHandler(OnClickDeleteConversion(conversion))
            },
            onShowDialog = { conversion ->
                eventHandler(OnClickSetConversion(conversion))
                eventHandler(OnClickShowDialog)
            },
            eventHandler = eventHandler
        )
    } else {
        InformationMessage(message = state.informationMessage)
    }
}

@Composable
private fun ConversionHistoryLazyColumn(
    lazyListState: LazyListState,
    state: HistoryState,
    onMarkConversionAsFavorite: (Conversion) -> Unit,
    onDeleteConversion: (Conversion) -> Unit,
    onShowDialog: (Conversion) -> Unit,
    eventHandler: (HistoryEvent) -> Unit
) {
    LaunchedEffect(key1 = state.searchQuery) {
        if (!lazyListState.isScrollInProgress) {
            lazyListState.animateScrollToItem(lazyListState.firstVisibleItemIndex)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        state = lazyListState,
        contentPadding = PaddingValues(
            horizontal = 30.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(
            items = state.conversionsHistory,
            key = { conversionHistory -> conversionHistory.id }
        ) { conversionHistory ->
            ConversionHistoryItem(
                conversionsHistory = conversionHistory,
                onMarkConversionAsFavorite = onMarkConversionAsFavorite,
                onDeleteConversion = onDeleteConversion,
                onShowDialog = onShowDialog,
                eventHandler = eventHandler
            )
        }
    }
}

@Composable
private fun ConversionHistoryItem(
    conversionsHistory: ConversionsHistory,
    onMarkConversionAsFavorite: (Conversion) -> Unit,
    onDeleteConversion: (Conversion) -> Unit,
    onShowDialog: (Conversion) -> Unit,
    eventHandler: (HistoryEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ConversionExchangeRateHeader(conversionsHistory = conversionsHistory)

        conversionsHistory.conversions.forEach { conversion ->
            key(conversion) {
                AnimatedDraggableConversion(
                    conversion = conversion,
                    onDeleteConversion = onDeleteConversion,
                    onShowDialog = onShowDialog,
                    eventHandler = eventHandler
                ) {
                    ConversionItemCard(
                        conversion = it,
                        onMarkConversionAsFavorite = onMarkConversionAsFavorite,
                        eventHandler = eventHandler
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversionExchangeRateHeader(
    conversionsHistory: ConversionsHistory
) {
    val exchangeRateDate = compareDates(conversionsHistory.currentExchangeRate.date)
    val bolivianPesos = conversionsHistory.currentExchangeRate.pesosBob
    val argentinePesos = conversionsHistory.currentExchangeRate.pesosArg

    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = exchangeRateDate,
            style = MaterialTheme.typography.titleLarge,
            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary
                    else Color(0xFF226054)
        )

        VerticalDivider(
            modifier = Modifier.height((11.5).dp),
            thickness = 2.dp,
            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary
                    else Color(0xFF226054)
        )

        Text(
            text = "$bolivianPesos BOB / 1 USD / $argentinePesos ARS",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun AnimatedDraggableConversion(
    conversion: Conversion,
    animationDuration: Int = 700,
    onDeleteConversion: (Conversion) -> Unit,
    onShowDialog: (Conversion) -> Unit,
    eventHandler: (HistoryEvent) -> Unit,
    content: @Composable (Conversion) -> Unit
) {
    var isRemoved by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDeleteConversion(conversion)
            //eventHandler(OnClickDeleteConversion(conversion))
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        DragToManageConversion(
            conversion = conversion,
            onClickDeleteConversion = { isRemoved = true },
            onClickShowEditConversionDialog = {
                onShowDialog(conversion)
                /*eventHandler(OnClickSetConversion(conversion))
                eventHandler(OnClickShowDialog)*/
            }
        ) {
            content(conversion)
        }
    }
}