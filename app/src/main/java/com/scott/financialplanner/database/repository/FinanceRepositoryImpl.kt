package com.scott.financialplanner.database.repository

import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.dao.CategoryDao
import com.scott.financialplanner.database.dao.ExpenseDao
import com.scott.financialplanner.database.entity.CategoryEntity
import com.scott.financialplanner.database.entity.toEntity
import com.scott.financialplanner.database.entity.toExpenses
import com.scott.financialplanner.provider.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class FinanceRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
    dispatcherProvider: DispatcherProvider
) : FinanceRepository {

    private val coroutineScope = CoroutineScope(dispatcherProvider.default())

    private val cachedCategories = mutableMapOf<String, List<Expense>>()

    private val _categories = MutableSharedFlow<Map<String, List<Expense>>>()
    override val categories = _categories.asSharedFlow()

    init {
        // Observe database changes to expenses and categories.
        expenseDao.allExpenses().combine(categoryDao.allCategories()) { expenses, categories ->
            cachedCategories.clear()
            categories.forEach { category ->
                val categoryExpenses = expenseDao.getCategoryExpenses(category.name).toExpenses()
                cachedCategories[category.name] = categoryExpenses
            }
            _categories.emit(cachedCategories)
        }.launchIn(coroutineScope)
    }

    override fun createCategory(name: String) {
        coroutineScope.launch {
            categoryDao.insertCategory(CategoryEntity(name = name))
        }
    }

    override fun createExpense(expense: Expense) {
        coroutineScope.launch {
            expenseDao.insertExpense(expense.toEntity())
        }
    }

    override fun deleteCategory(name: String) {
        coroutineScope.launch {
            categoryDao.deleteCategory(name)
            expenseDao.deleteAllCategoryExpenses(name)
        }
    }

    override fun deleteExpense(expense: Expense) {
        coroutineScope.launch {
            expenseDao.deleteExpense(expense.dateCreated.timeInMillis)
        }
    }

    override fun getCategoryExpenses(categoryName: String): List<Expense> =
        cachedCategories[categoryName] ?: emptyList()

    override fun editCategoryName(currentName: String, newName: String) {
        coroutineScope.launch {
            categoryDao.updateCategory(currentName, newName)
            expenseDao.updateCategoryNames(currentName, newName)
        }
    }

    override fun categoryExists(categoryName: String): Boolean =
        cachedCategories[categoryName] != null
}