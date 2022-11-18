package com.scott.financialplanner.database.repository

import com.scott.financialplanner.database.dao.CategoryDao
import com.scott.financialplanner.database.entity.CategoryEntity

internal class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override suspend fun getAllCategories(): List<String> =
        categoryDao.getCategories().map { it.name }

    override suspend fun insertCategory(name: String) =
        categoryDao.insertCategory(CategoryEntity(name = name))

    override suspend fun deleteCategory(name: String) =
        categoryDao.deleteCategory(name)

    override suspend fun updateCategoryName(currentName: String, newName: String) =
        categoryDao.updateCategory(currentName, newName)

    override suspend fun categoryExists(name: String): Boolean =
        categoryDao.categoryExists(name)
}