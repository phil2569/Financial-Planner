package com.scott.financialplanner.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ExpenseHistoryViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _actionChannel = Channel<ExpenseAction>(capacity = Channel.UNLIMITED)
    private val _expenseHistory = MutableStateFlow(listOf<Expense>())

    /**
     * An action channel the UI can send events to.
     */
    val actions: SendChannel<ExpenseAction> = _actionChannel

    /**
     * The category the expense history is associated with.
     */
    val categoryName = savedStateHandle.get<String>("category_extra") ?: ""

    /**
     * All expense history.
     */
    val expenseHistory = _expenseHistory.asStateFlow()

    init {
        _actionChannel.receiveAsFlow().onEach {
            when (it) {
                is ExpenseAction.DeleteExpenseClicked -> {
                    deleteExpenseItem(it.expense)
                }
            }
        }.launchIn(viewModelScope)

        // Observe changes to the repository.
        financeRepository.categories.onEach {
            refreshExpenses(it[categoryName])
        }.launchIn(viewModelScope)

        refreshExpenses(financeRepository.getCategoryExpenses(categoryName))
    }

    private fun refreshExpenses(expenses: List<Expense>?) {
        val expenseHistory = expenses?.sortedByDescending { it.dateCreated } ?: emptyList()
        _expenseHistory.tryEmit(expenseHistory)
    }

    private fun deleteExpenseItem(expense: Expense) {
        financeRepository.deleteExpense(expense)
    }

    sealed class ExpenseAction {
        data class DeleteExpenseClicked(val expense: Expense) : ExpenseAction()
    }
}