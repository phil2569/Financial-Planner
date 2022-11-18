package com.scott.financialplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scott.financialplanner.database.repository.FinanceRepository
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.AcceptNewCategoryClicked
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.CancelNewCategoryClicked
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.NewCategoryClicked
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * The ViewModel used to manage the state of the home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val financeRepository: FinanceRepository
): ViewModel() {

    private val _actionChannel = Channel<HomeScreenAction>(capacity = Channel.UNLIMITED)
    private val _homeScreenState = MutableStateFlow(HomeScreenState())

    /**
     * An action channel the UI can send events to.
     */
    val actions: SendChannel<HomeScreenAction> = _actionChannel

    /**
     * An observable containing the [HomeScreenState].
     */
    val homeScreenState = _homeScreenState.asStateFlow()

    init {
        _actionChannel.receiveAsFlow()
            .onEach { handleAction(it) }
            .launchIn(viewModelScope)
       /* financeRepository.categories.onEach {
            println("testingg categories: $it")
        }.launchIn(viewModelScope)*/
    }

    private fun handleAction(action: HomeScreenAction) {
        _homeScreenState.value = when (action) {
            NewCategoryClicked -> {
                _homeScreenState.value.copy(showNewCategoryInput = true)
            }
            is AcceptNewCategoryClicked -> {
                _homeScreenState.value.copy(showNewCategoryInput = false)
            }
            CancelNewCategoryClicked -> {
                _homeScreenState.value.copy(showNewCategoryInput = false)
            }
        }
    }

    /**
     * The state of the home screen.
     * @param showNewCategoryInput whether the new category text field should be displayed.
     */
    data class HomeScreenState(
        val showNewCategoryInput: Boolean = false
    )

    /**
     * Various actions this viewmodel will accept from the UI.
     */
    sealed class HomeScreenAction {
        object NewCategoryClicked: HomeScreenAction()
        data class AcceptNewCategoryClicked(val categoryName: String): HomeScreenAction()
        object CancelNewCategoryClicked: HomeScreenAction()
    }
}