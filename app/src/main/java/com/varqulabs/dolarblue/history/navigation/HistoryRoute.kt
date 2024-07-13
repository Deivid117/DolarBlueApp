package com.varqulabs.dolarblue.history.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.varqulabs.dolarblue.core.presentation.generics.loadings.CircularLoading
import com.varqulabs.dolarblue.core.presentation.utils.mvi.CollectEffect
import com.varqulabs.dolarblue.core.presentation.utils.mvi.toTriple
import com.varqulabs.dolarblue.history.presentation.HistoryEvent
import com.varqulabs.dolarblue.history.presentation.HistoryScreen
import com.varqulabs.dolarblue.history.presentation.HistoryUiEffect
import com.varqulabs.dolarblue.history.presentation.HistoryViewModel
import com.varqulabs.dolarblue.history.presentation.components.UpdateConversionDialog
import com.varqulabs.dolarblue.navigation.Routes

fun NavGraphBuilder.historyRoute(
    openDrawer: () -> Unit
) {
    composable<Routes.History> {

        val viewModel = hiltViewModel<HistoryViewModel>()
        val (state, eventHandler, uiEffect) = viewModel.toTriple()
        val context = LocalContext.current

        Box(modifier = Modifier.fillMaxSize()) {

            HistoryScreen(
                state = state,
                eventHandler = eventHandler
            )

            if (state.isLoading) { CircularLoading() }

            if (state.showDialog && state.selectedConversion != null) {
                UpdateConversionDialog(
                    state = state,
                    onClickHideDialog = {
                        eventHandler(HistoryEvent.OnClickHideDialog)
                    },
                    onClickUpdateConversion = { newName ->
                        eventHandler(HistoryEvent.OnSetNameConversion(newName))
                    }
                )
            }
        }

        CollectEffect(uiEffect = uiEffect) {
            when (it) {
                is HistoryUiEffect.OpenDrawer -> openDrawer()
                is HistoryUiEffect.ShowError -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}