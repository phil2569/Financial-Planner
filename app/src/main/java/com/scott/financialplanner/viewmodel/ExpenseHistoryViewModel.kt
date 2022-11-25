package com.scott.financialplanner.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.repository.ExpenseRepository
import com.scott.financialplanner.provider.DispatcherProvider
import com.scott.financialplanner.viewmodel.ExpenseHistoryViewModel.ExpenseHistory.Expenses
import com.scott.financialplanner.viewmodel.ExpenseHistoryViewModel.ExpenseHistory.NoExpenses
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseHistoryViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    dispatcherProvider: DispatcherProvider,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _expenseHistory: MutableStateFlow<ExpenseHistory> = MutableStateFlow(NoExpenses)

    val categoryName = savedStateHandle.get<String>("category_extra") ?: ""

    val expenseHistory = _expenseHistory.asStateFlow()

    init {
        viewModelScope.launch(dispatcherProvider.default()) {
            expenseRepository.getCategoryExpenses(categoryName).let {
                _expenseHistory.value = if (it.isEmpty()) NoExpenses else Expenses(it)
            }
        }
    }

    sealed class ExpenseHistory {
        object NoExpenses: ExpenseHistory()
        data class Expenses(val expenses: List<Expense>): ExpenseHistory()
    }
}