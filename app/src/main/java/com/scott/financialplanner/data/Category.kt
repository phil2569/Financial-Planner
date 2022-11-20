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

/**
 * A wrapper around a list of [Category].
 * This ensures a [StateFlow] will emit changes at the [Category] level.
 */
data class CategoryList(
    val categories: List<Category> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        return false
    }
}