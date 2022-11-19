package com.scott.financialplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scott.financialplanner.data.Category
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.repository.FinanceRepository
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.CreateNewCategory
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.CreateExpense
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.DeleteCategory
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.UpdateCategoryName
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * The ViewModel used to manage the state of the home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val financeRepository: FinanceRepository
) : ViewModel() {

    private val _actionChannel = Channel<HomeScreenAction>(capacity = Channel.UNLIMITED)
    private val _homeScreenUiState = MutableSharedFlow<HomeScreenUiState>()
    private val _categories = MutableStateFlow(emptyList<Category>())
    private val _totalMonthlyExpenses = MutableStateFlow(0f)

    /**
     * An action channel the UI can send events to.
     */
    val actions: SendChannel<HomeScreenAction> = _actionChannel

    /**
     * An observable containing the [HomeScreenUiState].
     */
    val homeScreenUiState: SharedFlow<HomeScreenUiState> = _homeScreenUiState

    /**
     * An observable containing the total monthly expenses.
     */
    val totalMonthlyExpenses = _totalMonthlyExpenses.asStateFlow()

    /**
     * An observable containing the list of created categories.
     */
    val categories = _categories.asStateFlow()

    init {
        _actionChannel.receiveAsFlow()
            .onEach { handleAction(it) }
            .launchIn(viewModelScope)
        financeRepository.categories.onEach {
            when {
                it.isEmpty() -> handleNoCategories()
                else -> handleCategories(it)
            }
        }.launchIn(viewModelScope)
    }

    private fun handleAction(action: HomeScreenAction) {
        when (action) {
            is CreateNewCategory -> {
                financeRepository.createCategory(action.categoryName)
            }
            is CreateExpense -> {
                val expense = Expense(
                    description = action.description,
                    amount = action.amount,
                    associatedCategory = action.associatedCategory,
                    dateCreated = Calendar.getInstance()
                )
                financeRepository.createExpense(expense)
            }
            is DeleteCategory -> financeRepository.deleteCategory(action.categoryName)
            is UpdateCategoryName -> financeRepository.editCategoryName(
                action.currentName,
                action.newName
            )
        }
    }

    private fun handleNoCategories() {
        _homeScreenUiState.tryEmit(HomeScreenUiState(showCategories = true))
    }

    private fun handleCategories(categories: List<Category>) {
        _homeScreenUiState.tryEmit(HomeScreenUiState(showCategories = false))
        _categories.value = arrayListOf<Category>().apply { addAll(categories) }
        updateTotalExpenses(categories = categories)
    }

    private fun updateTotalExpenses(categories: List<Category>) {
        var totalMonthlyExpenses = 0f
        categories.forEach {
            totalMonthlyExpenses += it.expenseTotal
        }
        _totalMonthlyExpenses.value = totalMonthlyExpenses
    }

    /**
     * The state of the home screen UI.
     * @param showCategories display the available categories.
     */
    data class HomeScreenUiState(
        val showCategories: Boolean = false
    )

    /**
     * Various actions this viewmodel will accept from the UI.
     */
    sealed class HomeScreenAction {
        data class CreateNewCategory(val categoryName: String) : HomeScreenAction()
        data class DeleteCategory(val categoryName: String) : HomeScreenAction()
        data class UpdateCategoryName(val currentName: String, val newName: String) :
            HomeScreenAction()

        data class CreateExpense(
            val associatedCategory: String,
            val description: String,
            val amount: Float
        ) : HomeScreenAction()
    }
}