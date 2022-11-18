package com.scott.financialplanner.database.repository

import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.dao.ExpenseDao
import com.scott.financialplanner.database.entity.ExpenseEntity
import java.util.*

internal class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao
): ExpenseRepository {

    override suspend fun getExpenseTotals(): Float {
        var total = 0f
        expenseDao.getAllExpenses().forEach {
            total += it.amount
        }
        return total
    }

    override suspend fun getCategoryExpenses(name: String) =
        expenseDao.getCategoryExpenses(name).toExpenses()

    override suspend fun deleteExpense(expense: Expense) =
        expenseDao.deleteExpense(expense.dateCreated.timeInMillis)

    override suspend fun deleteAllCategoryExpenses(categoryName: String) =
        expenseDao.deleteAllCategoryExpenses(categoryName)

    override suspend fun updateCategoryNames(currentName: String, newName: String) =
        expenseDao.updateCategoryNames(currentName, newName)

    override suspend fun insertExpense(expense: Expense) =
        expenseDao.insertExpense(expense.toEntity())
}

private fun List<ExpenseEntity>.toExpenses(): List<Expense> {
    val expenses = arrayListOf<Expense>()
    forEach { expenses.add(it.toExpense()) }
    return expenses
}

private fun ExpenseEntity.toExpense() = Expense(
    description = description,
    amount = amount,
    dateCreated = Calendar.getInstance().apply { timeInMillis = dateCreatedMillis },
    associatedCategory = associatedCategory
)

private fun Expense.toEntity() = ExpenseEntity(
    description = description,
    amount = amount,
    dateCreatedMillis = dateCreated.timeInMillis,
    associatedCategory = associatedCategory
)