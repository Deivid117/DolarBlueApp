package com.varqulabs.dolarblue.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varqulabs.dolarblue.R
import com.varqulabs.dolarblue.core.domain.DataState
import com.varqulabs.dolarblue.core.presentation.ui.UiText
import com.varqulabs.dolarblue.core.presentation.utils.mvi.MVIContract
import com.varqulabs.dolarblue.core.presentation.utils.mvi.mviDelegate
import com.varqulabs.dolarblue.core.domain.model.Conversion
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
) : ViewModel(), MVIContract<HistoryState, HistoryEvent, HistoryUiEffect> by mviDelegate(HistoryState()) {

    override fun eventHandler(event: HistoryEvent) {
        when (event) {
            is OnClickDrawer -> emitOpenDrawer()
            is Init -> executeGetConversionHistory()
            is OnClickGetFavoriteConversions -> handleGetConversions()
            is OnSetCurrencyColumnName -> setCurrencyColumnName(event.currencyColumnName)
            is OnSetSearchQuery -> setSearchQuery(event.searchQuery)
            is OnSearchConversion -> searchConversions()
            is OnClickClearTextField -> resetTextField()
            is OnClickShowDialog -> handleDialog(
                isVisible = true,
                selectedConversion = event.conversion
            )
            is OnClickHideDialog -> handleDialog(
                isVisible = false,
                selectedConversion = null
            )
            is OnConversionNameChange -> onConversionNameChange(event.newConversionName)
            is OnSetNameConversion -> canUpdateConversionName(event.name)
            is OnSetFavoriteConversion -> setFavoriteConversion(event.conversion)
            is OnShowSnackBar -> showSnackBar(event.conversionDeleted)
            is UndoConversionDelete -> resetUIState()
            is OnDeleteConversion -> confirmationDeleteConversion()
        }
    }

    private fun emitOpenDrawer() = viewModelScope.emitEffect(HistoryUiEffect.OpenDrawer)

    private fun emitError(error: String) = viewModelScope.emitEffect(HistoryUiEffect.ShowError(error))

    private fun handleGetConversions() {
        setShowFavoriteConversions()
        if (uiState.value.showFavoriteConversions) executeGetFavoritesConversionHistory()
        else executeGetConversionHistory()
    }

    private fun setShowFavoriteConversions() {
        updateUi { copy(showFavoriteConversions = !showFavoriteConversions) }
    }

    private fun executeGetConversionHistory() = viewModelScope.launch(Dispatchers.IO) {
        getConversionsHistoryUseCase.execute(Unit).collectLatest { dataState ->
            updateUiStateForDataState(
                dataState = dataState,
                isLoading = true
            ) { data ->
                updateUi {
                    copy(
                        isLoading = false,
                        conversionsHistory = data,
                        informationMessage = R.string.search_no_information_to_show,
                        showFavoriteConversions = false
                    )
                }
            }
        }
    }

    private fun executeGetFavoritesConversionHistory() = viewModelScope.launch(Dispatchers.IO) {
        getFavoriteConversionsHistoryUseCase.execute(Unit).collectLatest { dataState ->
            updateUiStateForDataState(
                dataState = dataState,
                isLoading = true
            ) { data ->
                updateUi {
                    copy(
                        isLoading = false,
                        conversionsHistory = data,
                        informationMessage = R.string.search_no_information_to_show
                    )
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

    private fun searchConversions() {
        with(uiState.value){
            if (searchQuery.isNotBlank()) {
                executeConversionHistorySearch(currencyColumnName, searchQuery)
            } else {
                executeGetConversionHistory()
            }
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
                updateUiStateForDataState(dataState) { data ->
                    updateUi {
                        copy(
                            conversionsHistory = data,
                            informationMessage = R.string.search_no_matches_found
                        )
                    }
                }
            }
        }

    private fun resetTextField() {
        updateUi { copy(searchQuery = "") }
        executeGetConversionHistory()
    }

    private fun handleDialog(isVisible: Boolean, selectedConversion: Conversion?) {
        updateUi {
            copy(
                isDialogVisible = isVisible,
                selectedConversion = selectedConversion,
                newConversionName = "",
                conversionNameError = null
            )
        }
    }

    private fun onConversionNameChange(newConversionName: String) {
        updateUi {
            copy(
                newConversionName = newConversionName,
                conversionNameError = validateConversionName(newConversionName)
            )
        }
    }

    private fun validateConversionName(conversionName: String): UiText? {
        return when {
            conversionName == uiState.value.selectedConversion?.name -> UiText.StringResource(R.string.error_same_name)
            conversionName.length < 2 -> UiText.StringResource(R.string.error_name_length)
            else -> null
        }
    }

    private fun validateAndSetErrors(currentState: HistoryState): Boolean {
        val emailError = validateConversionName(currentState.newConversionName)
        updateUi { copy(conversionNameError = emailError) }
        return emailError != null
    }

    private fun canUpdateConversionName(name: String) {
        val canUpdate = validateAndSetErrors(uiState.value)
        if (!canUpdate) {
            setConversionName(name)
        }
    }

    private fun setConversionName(name: String) {
        if (uiState.value.selectedConversion != null) {
            executeUpdateConversion(
                conversion = uiState.value.selectedConversion!!.copy(name = name),
                isLoading = true
            )
        }
    }

    private fun setFavoriteConversion(conversion: Conversion) {
        executeUpdateConversion(conversion.copy(isFavorite = !conversion.isFavorite))
    }

    private fun executeUpdateConversion(conversion: Conversion, isLoading: Boolean = false) =
        viewModelScope.launch(Dispatchers.IO) {
            updateConversionUseCase.execute(conversion).collectLatest { dataState ->
                updateUiStateForDataState(
                    dataState = dataState,
                    isLoading = isLoading
                ) {
                    updateUi {
                        copy(
                            isDialogVisible = false,
                            newConversionName = "",
                            conversionNameError = null
                        )
                    }
                }
            }
        }

    private fun showSnackBar(conversionDeleted: Conversion) {
        filterConversionHistoryList(conversionDeleted)
        updateUi {
            copy(
                isSnackBarVisible = true,
                recentlyConversionDeleted = conversionDeleted,
                showFilteredList = true
            )
        }
    }

    private fun filterConversionHistoryList(conversion: Conversion) {
        val filteredConversionsHistory =
            uiState.value.conversionsHistory.map { conversionHistory ->
                if (conversionHistory.conversions.contains(conversion)) {
                    conversionHistory.copy(
                        conversions = conversionHistory.conversions.filter { it != conversion }
                    )
                } else conversionHistory
            }
        updateUi { copy(filteredConversionsHistory = filteredConversionsHistory) }
    }

    private fun resetUIState() {
        updateUi {
            copy(
                isSnackBarVisible = false,
                recentlyConversionDeleted = null,
                filteredConversionsHistory = emptyList(),
                showFilteredList = false,
                isHeaderVisible = true
            )
        }
    }

    private fun confirmationDeleteConversion() {
        if (uiState.value.recentlyConversionDeleted != null) {
            executeDeleteConversion(uiState.value.recentlyConversionDeleted!!)
        }
    }

    private fun executeDeleteConversion(conversion: Conversion) =
        viewModelScope.launch(Dispatchers.IO) {
            deleteConversionUseCase.execute(conversion).collectLatest { dataState ->
                updateUiStateForDataState(dataState) {
                    updateUi {
                        executeExchangeRateConversionCount(conversion.currentExchangeId)
                        copy(
                            isSnackBarVisible = false,
                            recentlyConversionDeleted = null,
                            filteredConversionsHistory = emptyList(),
                            showFilteredList = false
                        )
                    }
                }
            }
        }

    private fun executeExchangeRateConversionCount(exchangeRateId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            getExchangeRateConversionCountUseCase.execute(exchangeRateId)
                .collectLatest { dataState ->
                    updateUiStateForDataState(dataState) { data ->
                        updateUi {
                            copy(conversionCount = data)
                        }
                        conversionCountAndDeleteExchangeRate(exchangeRateId)
                    }
                }
        }

    private fun conversionCountAndDeleteExchangeRate(exchangeRateId: Int) {
        if (uiState.value.conversionCount == 0) {
            executeDeleteExchangeRate(exchangeRateId)
        }
    }

    private fun executeDeleteExchangeRate(exchangeRateId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            deleteExchangeRateUseCase.execute(exchangeRateId).collectLatest { dataState ->
                updateUiStateForDataState(
                    dataState = dataState,
                    onError = { executeGetConversionHistory() }
                )
            }
        }

    private fun <T> updateUiStateForDataState(
        dataState: DataState<T>,
        isLoading: Boolean = false,
        onError: () -> Unit = {},
        onSuccess: (T) -> Unit = {}
    ) {
        updateUi {
            when (dataState) {
                DataState.Loading -> copy(isLoading = isLoading, isError = false)
                is DataState.Success -> {
                    onSuccess(dataState.data)
                    copy()
                }
                is DataState.Error, DataState.NetworkError -> {
                    emitError(dataState.getErrorMessage())
                    onError()
                    copy(isLoading = false, isError = true)
                }
            }
        }
    }
}