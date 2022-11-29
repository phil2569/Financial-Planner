package com.scott.financialplanner.database.dao

import androidx.room.*
import com.scott.financialplanner.database.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ExpenseDao {

    @Query("SELECT * FROM expenses")
    fun allExpenses(): Flow<List<ExpenseEntity>>

    @Insert
    fun insertExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE date_created_millis == :dateCreatedMillis")
    fun deleteExpense(dateCreatedMillis: Long)

    @Query("DELETE FROM expenses WHERE associated_category == :categoryName")
    fun deleteAllCategoryExpenses(categoryName: String)

    @Query("UPDATE expenses SET associated_category = :newName WHERE associated_category == :currentName")
    fun updateCategoryNames(currentName: String, newName: String)

    @Query("SELECT * FROM expenses WHERE expenses.associated_category == :categoryName")
    fun getCategoryExpenses(categoryName: String): List<ExpenseEntity>
}