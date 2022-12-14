package com.scott.financialplanner.data

import java.util.Calendar

/**
 * Models an expense item.
 * @property description The name of the expense.
 * @property amount The cost of the expense.
 * @property dateCreated The date the expense was created.
 * @property associatedCategory The category this expense is linked to.
 */
data class Expense(
    val description: String,
    val amount: Float,
    val dateCreated: Calendar,
    val associatedCategory: String
)

fun List<Expense>.sumOfExpenses(): Float {
    var total = 0f
    forEach { total += it.amount }
    return total
}