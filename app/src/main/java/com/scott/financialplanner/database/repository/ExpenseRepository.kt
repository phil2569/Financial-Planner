package com.scott.financialplanner.database.repository

import com.scott.financialplanner.data.Expense

/**
 * Responsible for managing all expenses.
 */
internal interface ExpenseRepository {

    /**
     * @return The total spent on all expenses.
     */
    suspend fun getExpenseTotals(): Float

    /**
     * Retrieves the expenses for the provided category name.
     * @param name the name of the category.
     * @return A list of all expenses associated with the category.
     */
    suspend fun getCategoryExpenses(name: String): List<Expense>

    /**
     * Deletes an expense.
     * @param expense The expense to be deleted.
     */
    suspend fun deleteExpense(expense: Expense)

    /**
     * Deletes all expenses associated with the [categoryName].
     * @param categoryName The name of the category.
     */
    suspend fun deleteAllCategoryExpenses(categoryName: String)

    /**
     * Updates expense's associated category to the [newName] if it matches the [currentName].
     * @param currentName the current name of the category.
     * @param newName the new name for the category.
     */
    suspend fun updateCategoryNames(currentName: String, newName: String)

    /**
     * Inserts an expense.
     * @param expense The expense to be inserted.
     */
    suspend fun insertExpense(expense: Expense)
}