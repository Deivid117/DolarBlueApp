package com.varqulabs.dolarblue.history.presentation

sealed class HistoryUiEffect {

    data object OpenDrawer : HistoryUiEffect()

    data class ShowError(val message: String) : HistoryUiEffect()
}