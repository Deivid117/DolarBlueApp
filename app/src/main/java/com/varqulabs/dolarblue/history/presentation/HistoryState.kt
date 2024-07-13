package com.varqulabs.dolarblue.history.presentation

import androidx.compose.runtime.Stable
import com.varqulabs.dolarblue.history.domain.model.Conversion
import com.varqulabs.dolarblue.history.domain.model.ConversionsHistory

@Stable
data class HistoryState(
    val reload: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val conversionsHistory: List<ConversionsHistory> = emptyList(),
    val showDialog: Boolean = false,
    val selectedConversion: Conversion? = null,
    val showFavoriteConversions: Boolean = false,
    val currencyColumnName: String = CurrencyTab.BOB.columnName,
    val searchQuery: String = "",
    val conversionCount: Int = 0,
    val informationMessage: String = "No hay información por mostrar",
)

enum class CurrencyTab(val tabName: String, val columnName: String) {
    BOB(tabName = "BOB", columnName = "conversion_table.pesosBob",),
    ARG(tabName = "ARG", columnName = "conversion_table.pesosArg"),
    USD(tabName = "USD", columnName = "conversion_table.dollar")
}
