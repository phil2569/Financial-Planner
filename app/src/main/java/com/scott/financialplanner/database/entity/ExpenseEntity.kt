package com.scott.financialplanner.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scott.financialplanner.data.Expense
import java.util.*

@Entity(tableName = "expenses")
internal data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "amount") val amount: Float,
    @ColumnInfo(name = "date_created_millis") val dateCreatedMillis: Long,
    // The category name associated with this expense.
    @ColumnInfo(name = "associated_category") val associatedCategory: String,
)

internal fun List<ExpenseEntity>.toExpenses(): List<Expense> {
    val expenses = arrayListOf<Expense>()
    forEach { expenses.add(it.toExpense()) }
    return expenses
}

internal fun ExpenseEntity.toExpense() = Expense(
    description = description,
    amount = amount,
    dateCreated = Calendar.getInstance().apply { timeInMillis = dateCreatedMillis },
    associatedCategory = associatedCategory
)

internal fun Expense.toEntity() = ExpenseEntity(
    description = description,
    amount = amount,
    dateCreatedMillis = dateCreated.timeInMillis,
    associatedCategory = associatedCategory
)