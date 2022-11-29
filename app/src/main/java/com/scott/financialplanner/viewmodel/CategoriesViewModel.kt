package com.scott.financialplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scott.financialplanner.data.Category
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.data.sumOfExpenses
import com.scott.financialplanner.database.repository.FinanceRepository
import com.scott.financialplanner.provider.DispatcherProvider
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryAction.NewCategoryClicked
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryAction.SaveNewExpenseClicked
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryAction.DeleteCategoryClicked
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryAction.ExpenseHistoryClicked
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryAction.UpdateCategoryClicked
import com.scott.financialplanner.viewmodel.CategoriesViewModel.LoadingState.Initialized
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryEvent.CategoryAlreadyExists
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryEvent.NavigateToExpenseHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * A ViewModel that manages available [Category]s and [Expense]s.
 */
@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _categories = MutableSharedFlow<List<Category>>()
    private val _actionChannel = Channel<CategoryAction>(capacity = Channel.UNLIMITED)
    private val _events = MutableSharedFlow<CategoryEvent>()
    private val _totalMonthlyExpenses = MutableStateFlow(0f)
    private val _categoriesLoadingState: MutableStateFlow<LoadingState> =
        MutableStateFlow(LoadingState.Initializing)

    /**
     * An action channel a consumer can send events to.
     */
    val actions: SendChannel<CategoryAction> = _actionChannel

    /**
     * Events a consumer can respond to.
     */
    val events = _events.asSharedFlow()

    /**
     * The loading state of the categories.
     */
    val categoryLoadingState = _categoriesLoadingState.asStateFlow()

    /**
     * An observable containing the total monthly expenses.
     */
    val totalMonthlyExpenses = _totalMonthlyExpenses.asStateFlow()

    /**
     * An observable containing the list of created categories.
     */
    val categories = _categories.asSharedFlow()

    init {
        _actionChannel.receiveAsFlow()
            .onEach { handleAction(it) }
            .launchIn(viewModelScope)
        financeRepository.categories.onEach {
            handleCategories(it)
        }.launchIn(viewModelScope)
    }

    private fun handleAction(action: CategoryAction) {
        when (action) {
            is NewCategoryClicked -> createCategory(action.categoryName)
            is SaveNewExpenseClicked -> createExpense(
                action.description,
                action.amount,
                action.associatedCategory
            )
            is DeleteCategoryClicked -> deleteCategory(action.categoryName)
            is UpdateCategoryClicked -> editCategoryName(action.currentName, action.newName)
            is ExpenseHistoryClicked -> navigateToExpenseHistory(action.categoryName)
        }
    }

    private suspend fun handleCategories(categoriesMap: Map<String, List<Expense>>) {
        val categories = categoriesMap.map {
            Category(it.key, it.value.sumOfExpenses())
        }
        updateTotalExpenses(categories = categories)
        _categories.emit(categories)
        _categoriesLoadingState.value = Initialized
    }

    private fun updateTotalExpenses(categories: List<Category>) {
        var totalMonthlyExpenses = 0f
        categories.forEach {
            totalMonthlyExpenses += it.expenseTotal
        }
        _totalMonthlyExpenses.value = totalMonthlyExpenses
    }

    private fun createCategory(categoryName: String) {
        viewModelScope.launch(dispatcherProvider.default()) {
            when {
                financeRepository.categoryExists(categoryName) -> {
                    _events.emit(CategoryAlreadyExists(categoryName))
                }
                else -> financeRepository.createCategory(categoryName)
            }
        }
    }

    private fun deleteCategory(categoryName: String) {
        financeRepository.deleteCategory(categoryName)
    }

    private fun editCategoryName(currentName: String, newName: String) {
        financeRepository.editCategoryName(currentName, newName)
    }

    private fun createExpense(
        expenseDescription: String,
        expenseAmount: String,
        associatedCategory: String
    ) {
        val expense = Expense(
            description = expenseDescription,
            amount = expenseAmount.toFloat(),
            associatedCategory = associatedCategory,
            dateCreated = Calendar.getInstance()
        )
        financeRepository.createExpense(expense)
    }

    private fun navigateToExpenseHistory(categoryName: String) {
        viewModelScope.launch {
            _events.emit(NavigateToExpenseHistory(categoryName))
        }
    }

    /**
     * Represents the initialization state of this viewModel.
     */
    sealed class LoadingState {
        object Initializing : LoadingState()
        object Initialized : LoadingState()
    }

    /**
     * View Model events for the consumer.
     */
    sealed class CategoryEvent {
        data class CategoryAlreadyExists(val categoryName: String) : CategoryEvent()
        data class NavigateToExpenseHistory(val categoryName: String) : CategoryEvent()
    }

    /**
     * Actions this view model will accept.
     */
    sealed class CategoryAction {
        data class NewCategoryClicked(val categoryName: String) : CategoryAction()
        data class DeleteCategoryClicked(val categoryName: String) : CategoryAction()
        data class UpdateCategoryClicked(val currentName: String, val newName: String) :
            CategoryAction()

        data class ExpenseHistoryClicked(val categoryName: String) : CategoryAction()
        data class SaveNewExpenseClicked(
            val associatedCategory: String,
            val description: String,
            val amount: String
        ) : CategoryAction()
    }
}