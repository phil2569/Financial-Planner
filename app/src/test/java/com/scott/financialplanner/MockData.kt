package com.scott.financialplanner

import com.scott.financialplanner.data.Category
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.entity.CategoryEntity
import com.scott.financialplanner.database.entity.ExpenseEntity
import java.util.*

internal fun buildCategory(
    name: String = "category",
    expenseTotal: Float = 0f
) = Category(name, expenseTotal)

internal fun buildExpense(
    description: String = "expense",
    amount: Float = 10f,
    dateCreated: Calendar = Calendar.getInstance(),
    associatedCategory: String = "category"
) = Expense(description, amount, dateCreated, associatedCategory)

internal fun Expense.toEntity() = ExpenseEntity(
    description = description,
    amount = amount,
    dateCreatedMillis = dateCreated.timeInMillis,
    associatedCategory = associatedCategory
)

internal fun Category.toEntity() = CategoryEntity(
    name = name
)