package com.scott.financialplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scott.financialplanner.data.Category
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.repository.FinanceRepository
import com.scott.financialplanner.provider.DispatcherProvider
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.CreateNewCategory
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.CreateExpense
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.DeleteCategory
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.UpdateCategoryName
import com.scott.financialplanner.viewmodel.HomeViewModel.CategoryState.Categories
import com.scott.financialplanner.viewmodel.HomeViewModel.CategoryState.Initializing
import com.scott.financialplanner.viewmodel.HomeViewModel.CategoryState.NoCategories
import com.scott.financialplanner.viewmodel.HomeViewModel.UhOh.BlankCategory
import com.scott.financialplanner.viewmodel.HomeViewModel.UhOh.CategoryAlreadyExists
import com.scott.financialplanner.viewmodel.HomeViewModel.UhOh.NoUhOh
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * The ViewModel used to manage the state of the home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _actionChannel = Channel<HomeScreenAction>(capacity = Channel.UNLIMITED)
    private val _categoryLoadingState: MutableStateFlow<CategoryState> = MutableStateFlow(Initializing)
    private val _uhOhs: MutableStateFlow<UhOh> = MutableStateFlow(NoUhOh)
    private val _categories = MutableStateFlow(emptyList<Category>())
    private val _totalMonthlyExpenses = MutableStateFlow(0f)

    /**
     * An action channel the UI can send events to.
     */
    val actions: SendChannel<HomeScreenAction> = _actionChannel

    /**
     * The loading state of the categories.
     */
    val categoryLoadingState = _categoryLoadingState.asStateFlow()

    /**
     * An observable containing the total monthly expenses.
     */
    val totalMonthlyExpenses = _totalMonthlyExpenses.asStateFlow()

    /**
     * An observable containing the list of created categories.
     */
    val categories = _categories.asStateFlow()

    /**
     * Any errors that may occur that a consumer can respond to.
     */
    val uhOhs = _uhOhs.asStateFlow()

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
            is CreateNewCategory -> attemptCategoryCreation(action.categoryName)
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
        _categoryLoadingState.value = NoCategories
    }

    private fun handleCategories(categories: List<Category>) {
        _categoryLoadingState.value = Categories
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

    private fun attemptCategoryCreation(categoryName: String) {
        viewModelScope.launch(dispatcherProvider.default()) {
            when {
                categoryName.isEmpty() -> _uhOhs.value = BlankCategory
                financeRepository.categoryExists(categoryName) -> {
                    _uhOhs.value = CategoryAlreadyExists(categoryName)
                }
                else -> financeRepository.createCategory(categoryName)
            }
        }
    }

    /**
     * Represents the loading state of the [categories].
     */
    sealed class CategoryState {
        object Initializing : CategoryState()
        object Categories : CategoryState()
        object NoCategories : CategoryState()
    }

    /**
     * Various errors for the consumer to handle.
     */
    sealed class UhOh {
        object NoUhOh : UhOh()
        object BlankCategory : UhOh()
        data class CategoryAlreadyExists(val categoryName: String) : UhOh()
    }

    /**
     * Various actions this view model will accept from the UI.
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