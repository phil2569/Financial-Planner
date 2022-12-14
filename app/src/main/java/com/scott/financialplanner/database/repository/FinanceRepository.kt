package com.scott.financialplanner.database.repository

import com.scott.financialplanner.data.Category
import com.scott.financialplanner.data.Expense
import kotlinx.coroutines.flow.SharedFlow

/**
 * A repository responsible for managing all [Category]s and their [Expense]s.
 */
interface FinanceRepository {

    /**
     * A shared flow emitting a map of category to it's expenses.
     */
    val categories: SharedFlow<Map<String, List<Expense>>>

    /**
     * Creates a new [Category].
     * @param name the name of the category.
     */
    fun createCategory(name: String)

    /**
     * Creates a new expense.
     * @param expense the new expense
     */
    fun createExpense(expense: Expense)

    /**
     * Deletes the category if it exists. Otherwise, does nothing.
     * @param name the name of the category to be deleted.
     */
    fun deleteCategory(name: String)

    /**
     * Deletes an expense if it exists. Otherwise, does nothing.
     * @param expense The expense to be deleted.
     * @return true if it was deleted successfully.
     */
    fun deleteExpense(expense: Expense)

    /**
     * Retrieves the expenses for the provided category name.
     * @param categoryName the name of the category.
     * @return A list of all expenses associated with the category.
     */
    fun getCategoryExpenses(categoryName: String): List<Expense>

    /**
     * Edits the category name if it exists. Otherwise, does nothing.
     * @param currentName the current name of the category.
     * @param newName the new name for the category.
     */
    fun editCategoryName(currentName: String, newName: String)

    /**
     * @return true if the category exists.
     * @param categoryName the name of the category.
     */
    fun categoryExists(categoryName: String): Boolean
}