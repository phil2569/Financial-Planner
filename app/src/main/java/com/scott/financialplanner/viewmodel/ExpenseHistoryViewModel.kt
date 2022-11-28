package com.scott.financialplanner.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.repository.ExpenseRepository
import com.scott.financialplanner.database.repository.FinanceRepository
import com.scott.financialplanner.provider.DispatcherProvider
import com.scott.financialplanner.viewmodel.ExpenseHistoryViewModel.ExpenseHistory.Expenses
import com.scott.financialplanner.viewmodel.ExpenseHistoryViewModel.ExpenseHistory.NoExpenses
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseHistoryViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    private val dispatcherProvider: DispatcherProvider,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _actionChannel = Channel<ExpenseAction>(capacity = Channel.UNLIMITED)
    private val _expenseHistory: MutableSharedFlow<ExpenseHistory> = MutableStateFlow(NoExpenses)

    /**
     * An action channel the UI can send events to.
     */
    val actions: SendChannel<ExpenseAction> = _actionChannel

    val categoryName = savedStateHandle.get<String>("category_extra") ?: ""

    val expenseHistory = _expenseHistory.asSharedFlow()

    init {
        _actionChannel.receiveAsFlow().onEach {
            when (it) {
                is ExpenseAction.DeleteExpenseClicked -> {
                    deleteExpenseItem(it.expense)
                    loadExpenses()
                }
            }
        }.launchIn(viewModelScope)
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch(dispatcherProvider.default()) {
            financeRepository.getCategoryExpenses(categoryName).let {
                val expenseHistory = when {
                    it.isEmpty() -> NoExpenses
                    else -> Expenses(it.sortedByDescending { it.dateCreated })
                }
                _expenseHistory.emit(expenseHistory)
            }
        }
    }

    private fun deleteExpenseItem(expense: Expense) {
        viewModelScope.launch(dispatcherProvider.default()) {
            financeRepository.deleteExpense(expense)
        }
    }

    sealed class ExpenseAction {
        data class DeleteExpenseClicked(val expense: Expense): ExpenseAction()
    }

    sealed class ExpenseHistory {
        object NoExpenses: ExpenseHistory()
        data class Expenses(val expenses: List<Expense>): ExpenseHistory()
    }
}