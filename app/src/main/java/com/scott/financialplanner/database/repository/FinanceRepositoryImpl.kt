package com.scott.financialplanner.database.repository

import com.scott.financialplanner.data.Category
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.provider.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

internal class FinanceRepositoryImpl(
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository,
    dispatcherProvider: DispatcherProvider
) : FinanceRepository {

    private val coroutineScope = CoroutineScope(dispatcherProvider.default())

    private val cachedCategories = arrayListOf<Category>()

    private val _categories = MutableSharedFlow<List<Category>>()
    override val categories: SharedFlow<List<Category>> = _categories

    init {
        coroutineScope.launch {
            categoryRepository.getAllCategories().forEach { category ->
                val categoryExpenses = expenseRepository.getCategoryExpenses(category)
                cachedCategories.add(
                    Category(
                        name = category,
                        expenseTotal = categoryExpenses.sumOfExpenses()
                    )
                )
            }
            _categories.emit(cachedCategories)
        }
    }

    override fun createCategory(name: String) {
        coroutineScope.launch {
            cachedCategories.add(Category(name = name, 0f))
            categoryRepository.insertCategory(name)
            _categories.emit(cachedCategories)
        }
    }

    override fun createExpense(expense: Expense) {
        coroutineScope.launch {
            getCachedCategory(expense.associatedCategory)?.let {
                it.expenseTotal += expense.amount
                expenseRepository.insertExpense(expense)
                _categories.emit(cachedCategories)
            }
        }
    }

    override fun deleteCategory(name: String) {
        coroutineScope.launch {
            removeCachedCategory(name)
            categoryRepository.deleteCategory(name)
            expenseRepository.deleteAllCategoryExpenses(name)
            _categories.emit(cachedCategories)
        }
    }

    override fun deleteExpense(expense: Expense) {
        coroutineScope.launch {
            getCachedCategory(expense.associatedCategory)?.let {
                it.expenseTotal -= expense.amount
                expenseRepository.deleteExpense(expense)
                _categories.emit(cachedCategories)
            }
        }
    }

    override fun editCategoryName(currentName: String, newName: String) {
        coroutineScope.launch {
            getCachedCategory(currentName)?.let {
                it.name = newName
                categoryRepository.updateCategoryName(currentName, newName)
                expenseRepository.updateCategoryNames(currentName, newName)
                _categories.emit(cachedCategories)
            }
        }
    }

    override suspend fun categoryExists(categoryName: String): Boolean =
        categoryRepository.categoryExists(categoryName)

    /**
     * @return the category from [cachedCategories] if it exists. Otherwise, null.
     */
    private fun getCachedCategory(name: String): Category? = cachedCategories.find { it.name == name }

    /**
     * Removes the category from [cachedCategories] if it exists.
     */
    private fun removeCachedCategory(name: String) = cachedCategories.removeIf { it.name == name }
}

private fun List<Expense>.sumOfExpenses(): Float {
    var total = 0f
    forEach { total += it.amount }
    return total
}