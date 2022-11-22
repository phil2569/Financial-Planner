package com.scott.financialplanner.data

/**
 * Models a Category.
 * @property name A unique name for the category.
 * @property expenseTotal The sum of all the expenses.
 */
data class Category(
    var name: String,
    var expenseTotal: Float
)