package com.scott.financialplanner.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.repository.ExpenseRepository
import com.scott.financialplanner.provider.DispatcherProvider
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

    private val categoryName = savedStateHandle.get<String>("category_extra")!!

    private val _expenses = MutableStateFlow(emptyList<Expense>())

    val expenses = _expenses.asStateFlow()

    init {
        viewModelScope.launch(dispatcherProvider.default()) {
            _expenses.value = expenseRepository.getCategoryExpenses(categoryName)
        }
    }
}