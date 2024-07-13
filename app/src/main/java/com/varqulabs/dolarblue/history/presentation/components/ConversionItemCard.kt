package com.varqulabs.dolarblue.history.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.varqulabs.dolarblue.core.presentation.utils.modifier.clickableSingle
import com.varqulabs.dolarblue.core.presentation.utils.modifier.clickableSingleWithOutRipple
import com.varqulabs.dolarblue.history.domain.model.Conversion
import com.varqulabs.dolarblue.history.presentation.HistoryEvent
import com.varqulabs.dolarblue.history.presentation.components.auxiliar.BorderFavoriteIconNegative
import com.varqulabs.dolarblue.history.presentation.components.auxiliar.BorderFavoriteIconPositive
import com.varqulabs.dolarblue.history.presentation.components.auxiliar.FavoriteIconNegative
import com.varqulabs.dolarblue.history.presentation.components.auxiliar.FavoriteIconPositive

@Composable
fun ConversionItemCard(
    conversion: Conversion,
    onMarkConversionAsFavorite: (Conversion) -> Unit,
    eventHandler: (HistoryEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp)
    ) {
        Column(
            modifier = Modifier.padding(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ConversionCardHeader(
                conversion = conversion,
                onMarkConversionAsFavorite = onMarkConversionAsFavorite,
                eventHandler
            )

            ConversionCardBody(conversion)
        }
    }
}

@Composable
private fun ConversionCardHeader(
    conversion: Conversion,
    onMarkConversionAsFavorite: (Conversion) ->  Unit,
    eventHandler: (HistoryEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSystemInDarkTheme()) Color(0xFF545051) else Color.White,
                shape = RoundedCornerShape(5.dp)
            )
            .border(
                1.dp,
                color = if (isSystemInDarkTheme()) Color.Transparent else Color(0xFFAFAFAF),
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 15.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Conversión:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.inverseSurface
        )

        Text(
            text = conversion.name,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.inverseSurface
        )

        FavoriteConversionIcon(
            conversion = conversion,
            onMarkConversionAsFavorite = onMarkConversionAsFavorite,
            eventHandler = eventHandler
        )
    }
}

@Composable
private fun FavoriteConversionIcon(
    conversion: Conversion,
    onMarkConversionAsFavorite: (Conversion) -> Unit,
    eventHandler: (HistoryEvent) -> Unit
) {
    val iconFavorite = if (isSystemInDarkTheme()) FavoriteIconNegative else FavoriteIconPositive
    val iconFavoriteBorder = if (isSystemInDarkTheme()) BorderFavoriteIconNegative else BorderFavoriteIconPositive

    Icon(
        modifier = Modifier
            .size(24.dp)
            .clickableSingleWithOutRipple {
                onMarkConversionAsFavorite(conversion)
                /*eventHandler(HistoryEvent.OnClickSetConversion(conversion))
                eventHandler(HistoryEvent.OnSetFavoriteConversion(!conversion.isFavorite))*/
            },
        imageVector = if (conversion.isFavorite) iconFavorite else iconFavoriteBorder,
        contentDescription = "Favorite icon",
        tint = Color.Unspecified
    )
}

@Composable
private fun ConversionCardBody(conversion: Conversion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyRateValue(currencyName = "BOB", value = conversion.pesosBob)

        CurrencyRateValue(currencyName = "USD", value = conversion.dollar)

        CurrencyRateValue(currencyName = "ARG", value = conversion.pesosArg)
    }
}

@Composable
private fun CurrencyRateValue(
    currencyName: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = currencyName,
            style = MaterialTheme.typography.titleSmall,
            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary
                    else Color(0xFF226054)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}