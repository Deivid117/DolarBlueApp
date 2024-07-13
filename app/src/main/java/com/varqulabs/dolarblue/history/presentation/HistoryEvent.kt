package com.varqulabs.dolarblue.history.presentation

import com.varqulabs.dolarblue.history.domain.model.Conversion

sealed interface HistoryEvent {

    data object OnClickDrawer : HistoryEvent

    data class OnClickSetConversion(val conversion: Conversion) : HistoryEvent

    data object Init : HistoryEvent

    data object OnClickGetFavoriteConversions : HistoryEvent

    data class OnSetFavoriteConversion(val isFavorite: Boolean) : HistoryEvent

    data class OnSetNameConversion(val name: String) : HistoryEvent

    data class OnClickDeleteConversion(val conversion: Conversion) : HistoryEvent

    data object OnSearchConversion : HistoryEvent

    data class OnSetCurrencyColumnName(val currencyColumnName: String) : HistoryEvent

    data class OnSetSearchQuery(val searchQuery: String) : HistoryEvent

    data object OnClickClearTextField : HistoryEvent

    data object OnClickShowDialog : HistoryEvent

    data object OnClickHideDialog : HistoryEvent
}