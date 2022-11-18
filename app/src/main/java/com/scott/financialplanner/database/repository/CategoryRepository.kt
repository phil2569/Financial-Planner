package com.scott.financialplanner.database.repository

/**
 * Responsible for managing all categories.
 */
internal interface CategoryRepository {

    /**
     * @return A list containing all existing categories.
     */
    suspend fun getAllCategories(): List<String>

    /**
     * Inserts a new category.
     * @param name The name of the category.
     */
    suspend fun insertCategory(name: String)

    /**
     * Deletes the category.
     * @param name the name of the category to be deleted.
     */
    suspend fun deleteCategory(name: String)

    /**
     * Updates the category name.
     * @param currentName the current name of the category.
     * @param newName the new name for the category.
     */
    suspend fun updateCategoryName(currentName: String, newName: String)

    /**
     * @param name the name of the category.
     * @return true if the category exists
     */
    suspend fun categoryExists(name: String): Boolean
}