package com.varqulabs.dolarblue.history.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.varqulabs.dolarblue.R
import com.varqulabs.dolarblue.core.presentation.utils.modifier.clearFocusOnKeyboardDismiss
import com.varqulabs.dolarblue.history.presentation.CurrencyTab
import com.varqulabs.dolarblue.history.presentation.HistoryEvent
import com.varqulabs.dolarblue.history.presentation.HistoryEvent.*
import com.varqulabs.dolarblue.history.presentation.HistoryState
import com.varqulabs.dolarblue.history.presentation.components.auxiliar.ClearIconNegative
import com.varqulabs.dolarblue.history.presentation.components.auxiliar.ClearIconPositive
import com.varqulabs.dolarblue.history.presentation.components.auxiliar.SearchIconNegative
import com.varqulabs.dolarblue.history.presentation.components.auxiliar.SearchIconPositive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HistorySearchCard(
    state: HistoryState,
    eventHandler: (HistoryEvent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier.wrapContentHeight(),
        shape = RoundedCornerShape(
            bottomStart = 30.dp,
            bottomEnd = 30.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF226054))
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 30.dp,
                vertical = 20.dp
            )
        ) {
            SearchConversionLabels()

            Spacer(modifier = Modifier.height(8.dp))

            SearchTextField(
                state = state,
                searchValue = state.searchQuery,
                onValueChange = {
                    eventHandler(OnSetSearchQuery(it))
                    coroutineScope.launch {
                        delay(1500)
                        eventHandler(OnSearchConversion)
                    }
                },
                onClearValue = { eventHandler(OnClickClearTextField) },
                onSearchConversions = { eventHandler(OnSearchConversion) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            CurrencyTabRow(
                onCurrencyColumnSelected = {
                    eventHandler(OnSetCurrencyColumnName(it))
                }
            )
        }
    }
}

@Composable
private fun SearchConversionLabels() {
    Column(verticalArrangement = Arrangement.spacedBy(35.dp)) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.copy_history),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.secondaryContainer
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary))

            Text(
                text = "Inserte el nombre de la conversión o la cantidad que esté buscando",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }
}

@Composable
private fun SearchTextField(
    state: HistoryState,
    searchValue: String,
    onValueChange: (String) -> Unit,
    onClearValue: () -> Unit,
    onSearchConversions: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val iconSearch = if (isSystemInDarkTheme()) SearchIconNegative else SearchIconPositive
    val iconClear = if (isSystemInDarkTheme()) ClearIconNegative else ClearIconPositive

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .clearFocusOnKeyboardDismiss(),
        value = searchValue,
        onValueChange = { changedValue ->
            onValueChange(changedValue)
        },
        singleLine = true,
        trailingIcon = {
            if (state.searchQuery.isNotEmpty()) {
                IconButton(onClick = {
                    onClearValue()
                }) {
                    Icon(imageVector = iconClear, contentDescription = "", tint = Color.Unspecified)
                }
            } else {
                Icon(
                    imageVector = iconSearch,
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        },
        textStyle = MaterialTheme.typography.titleSmall,
        keyboardOptions = KeyboardOptions(
            imeAction = if (state.searchQuery.isNotEmpty()) ImeAction.Search else ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone =  { keyboardController?.hide() },
            onSearch = {
                onSearchConversions()
                keyboardController?.hide()
            }
        )
    )
}

@Composable
private fun CurrencyTabRow(
    onCurrencyColumnSelected: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val currencyTabs = listOf(
        CurrencyTab.BOB,
        CurrencyTab.USD,
        CurrencyTab.ARG
    )

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        indicator = { tabPositions ->
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = MaterialTheme.colorScheme.primary,
                height = 1.dp
            )
        },
        divider = {}
    ) {
        currencyTabs.forEachIndexed { index, currencyTab ->

            val textStyle =
                if (selectedTabIndex == index)
                    MaterialTheme.typography.titleLarge
                else
                    MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal)

            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    onCurrencyColumnSelected(currencyTab.columnName)
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = currencyTab.tabName,
                    style = textStyle
                )
            }
        }
    }
}