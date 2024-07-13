package com.varqulabs.dolarblue.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varqulabs.dolarblue.core.domain.DataState
import com.varqulabs.dolarblue.core.presentation.utils.mvi.MVIContract
import com.varqulabs.dolarblue.core.presentation.utils.mvi.mviDelegate
import com.varqulabs.dolarblue.history.domain.model.Conversion
import com.varqulabs.dolarblue.history.domain.model.QueryAndCurrency
import com.varqulabs.dolarblue.history.domain.useCases.DeleteConversionUseCase
import com.varqulabs.dolarblue.history.domain.useCases.DeleteExchangeRateUseCase
import com.varqulabs.dolarblue.history.domain.useCases.UpdateConversionUseCase
import com.varqulabs.dolarblue.history.domain.useCases.GetConversionsHistoryFlowUseCase
import com.varqulabs.dolarblue.history.domain.useCases.GetFavoriteConversionsHistoryUseCase
import com.varqulabs.dolarblue.history.domain.useCases.GetExchangeRateConversionCountUseCase
import com.varqulabs.dolarblue.history.domain.useCases.SearchConversionsHistoryUseCase
import com.varqulabs.dolarblue.history.presentation.HistoryEvent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getConversionsHistoryUseCase: GetConversionsHistoryFlowUseCase,
    private val searchConversionsHistoryUseCase: SearchConversionsHistoryUseCase,
    private val updateConversionUseCase: UpdateConversionUseCase,
    private val getFavoriteConversionsHistoryUseCase: GetFavoriteConversionsHistoryUseCase,
    private val deleteConversionUseCase: DeleteConversionUseCase,
    private val getExchangeRateConversionCountUseCase: GetExchangeRateConversionCountUseCase,
    private val deleteExchangeRateUseCase: DeleteExchangeRateUseCase
) : ViewModel(),
    MVIContract<HistoryState, HistoryEvent, HistoryUiEffect> by mviDelegate(HistoryState()) {

    override fun eventHandler(event: HistoryEvent) {
        when (event) {
            is OnClickDrawer -> emitOpenDrawer()
            is OnClickSetConversion -> setConversion(event.conversion)
            is Init -> executeGetConversionHistory()
            is OnClickGetFavoriteConversions -> {
                updateUi { copy(showFavoriteConversions = !showFavoriteConversions) }
                showConversions(uiState.value.showFavoriteConversions)
            }
            is OnSetFavoriteConversion -> setFavoriteConversion(event.isFavorite)
            is OnSetNameConversion -> setConversionName(event.name)
            is OnClickDeleteConversion -> executeDeleteConversion(event.conversion)
            is OnSearchConversion -> searchConversions(
                searchQuery = uiState.value.searchQuery,
                currencyColumnName = uiState.value.currencyColumnName
            )
            is OnClickShowDialog -> updateUi { copy(showDialog = true) }
            is OnClickHideDialog -> updateUi { copy(showDialog = false) }
            is OnSetCurrencyColumnName -> setCurrencyColumnName(event.currencyColumnName)
            is OnSetSearchQuery -> setSearchQuery(event.searchQuery)
            is OnClickClearTextField -> {
                updateUi { copy(searchQuery = "") }
                executeGetConversionHistory()
            }
        }
    }

    private fun setConversion(conversion: Conversion) {
        updateUi { copy(selectedConversion = conversion) }
    }

    private fun showConversions(showFavoriteConversions: Boolean) {
        if (showFavoriteConversions) {
            executeGetFavoritesConversionHistory()
        } else {
            executeGetConversionHistory()
        }
    }

    private fun executeGetConversionHistory() = viewModelScope.launch(Dispatchers.IO) {
        getConversionsHistoryUseCase.execute(Unit).collectLatest { dataState ->
            updateUi {
                when (dataState) {
                    is DataState.Loading -> copy(isLoading = false)
                    is DataState.Success -> copy(
                        isLoading = false,
                        conversionsHistory = dataState.data
                    ).also { disableReload() }
                    is DataState.Error -> {
                        emitError(dataState.getErrorMessage())
                        copy(isError = true)
                    }
                    is DataState.NetworkError -> {
                        emitError(dataState.getErrorMessage())
                        copy(isError = true)
                    }
                }
            }
        }
    }

    private fun executeGetFavoritesConversionHistory() = viewModelScope.launch(Dispatchers.IO) {
        getFavoriteConversionsHistoryUseCase.execute(Unit).collectLatest { dataState ->
            updateUi {
                when (dataState) {
                    is DataState.Loading -> copy(isLoading = true)
                    is DataState.Success -> copy(
                        isLoading = false,
                        conversionsHistory = dataState.data
                    )
                    is DataState.Error -> {
                        emitError(dataState.getErrorMessage())
                        copy(isError = true)
                    }
                    is DataState.NetworkError -> {
                        emitError(dataState.getErrorMessage())
                        copy(isError = true)
                    }
                }
            }
        }
    }

    private fun setFavoriteConversion(isFavorite: Boolean) {
        if (uiState.value.selectedConversion != null) {
            executeUpdateConversion(uiState.value.selectedConversion!!.copy(isFavorite = isFavorite))
        }
    }

    private fun setConversionName(name: String) {
        if (uiState.value.selectedConversion != null) {
            executeUpdateConversion(uiState.value.selectedConversion!!.copy(name = name))
        }
    }

    private fun executeUpdateConversion(conversion: Conversion) =
        viewModelScope.launch(Dispatchers.IO) {
            updateConversionUseCase.execute(conversion).collectLatest { dataState ->
                updateUi {
                    when (dataState) {
                        is DataState.Loading -> copy(isLoading = true)
                        is DataState.Success -> copy(
                            isLoading = false,
                            showDialog = false
                        )
                        is DataState.Error -> {
                            emitError(dataState.getErrorMessage())
                            copy(isError = true)
                        }
                        is DataState.NetworkError -> {
                            emitError(dataState.getErrorMessage())
                            copy(isError = true)
                        }
                    }
                }
            }
        }

    private fun executeDeleteConversion(conversion: Conversion) =
        viewModelScope.launch(Dispatchers.IO) {
            deleteConversionUseCase.execute(conversion).collectLatest { dataState ->
                updateUi {
                    when (dataState) {
                        is DataState.Loading -> copy(isLoading = true)
                        is DataState.Success -> {
                            executeExchangeRateConversionCount(conversion.currentExchangeId)
                            copy(isLoading = false)
                        }
                        is DataState.Error -> {
                            emitError(dataState.getErrorMessage())
                            copy(isError = true)
                        }
                        is DataState.NetworkError ->{
                            emitError(dataState.getErrorMessage())
                            copy(isError = true)
                        }
                    }
                }
            }
        }

    private fun conversionCountAndDeleteExchangeRate(exchangeRateId: Int) {
        if (uiState.value.conversionCount == 0) {
            executeDeleteExchangeRate(exchangeRateId)
        }
    }

    private fun executeExchangeRateConversionCount(exchangeRateId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            getExchangeRateConversionCountUseCase.execute(exchangeRateId)
                .collectLatest { dataState ->
                    when (dataState) {
                        is DataState.Loading -> updateUi { copy(isLoading = true) }
                        is DataState.Success -> {
                            updateUi {
                                copy(
                                    isLoading = false,
                                    conversionCount = dataState.data
                                )
                            }
                            conversionCountAndDeleteExchangeRate(exchangeRateId)
                        }
                        is DataState.Error -> updateUi {
                            emitError(dataState.getErrorMessage())
                            copy(isError = true)
                        }
                        is DataState.NetworkError -> updateUi {
                            emitError(dataState.getErrorMessage())
                            copy(isError = true)
                        }
                    }
                }
        }

    private fun executeDeleteExchangeRate(exchangeRateId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            deleteExchangeRateUseCase.execute(exchangeRateId).collectLatest { dataState ->
                updateUi {
                    when (dataState) {
                        is DataState.Loading -> copy(isLoading = true)
                        is DataState.Success -> copy(isLoading = false)
                        is DataState.Error -> {
                            emitError(dataState.getErrorMessage())
                            copy(isError = true)
                        }
                        is DataState.NetworkError -> {
                            emitError(dataState.getErrorMessage())
                            copy(isError = true)
                        }
                    }
                }
            }
        }

    private fun setCurrencyColumnName(currencyColumnName: String) {
        updateUi { copy(currencyColumnName = currencyColumnName) }
    }

    private fun setSearchQuery(searchQuery: String) {
        updateUi { copy(searchQuery = searchQuery) }
    }

    private fun searchConversions(searchQuery: String, currencyColumnName: String) {
        if (searchQuery.isNotBlank() || searchQuery.isNotEmpty()) {
            executeConversionHistorySearch(currencyColumnName, searchQuery)
        } else {
            executeGetConversionHistory()
        }
    }

    private fun executeConversionHistorySearch(currencyColumnName: String, searchQuery: String) =
        viewModelScope.launch(Dispatchers.IO) {
            searchConversionsHistoryUseCase.execute(
                QueryAndCurrency(
                    currencyColumnName = currencyColumnName,
                    searchQuery = searchQuery
                )
            ).collectLatest { dataState ->
                updateUi {
                    when (dataState) {
                        is DataState.Loading -> copy(isLoading = false)
                        is DataState.Success -> copy(
                            isLoading = false,
                            conversionsHistory = dataState.data,
                            informationMessage = if(dataState.data.isEmpty()) "No se encontraron coincidencias" else ""
                        )
                        is DataState.Error ->{
                            emitError(dataState.getErrorMessage())
                            copy(isError = true)
                        }

                        is DataState.NetworkError -> {
                            emitError(dataState.getErrorMessage())
                            copy(isError = true)
                        }
                    }
                }
            }
        }

    private fun disableReload() = updateUi { copy(reload = false) }

    private fun emitOpenDrawer() = viewModelScope.emitEffect(HistoryUiEffect.OpenDrawer)

    private fun emitError(error: String) = viewModelScope.emitEffect(HistoryUiEffect.ShowError(error))

}

