package com.scott.financialplanner.database.dao

import androidx.room.*
import com.scott.financialplanner.database.entity.CategoryEntity

@Dao
internal interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun getCategories(): List<CategoryEntity>

    @Insert
    fun insertCategory(categoryEntity: CategoryEntity)

    @Query("DELETE FROM categories WHERE name == :name")
    fun deleteCategory(name: String)

    @Query("UPDATE categories SET name = :newName WHERE name == :currentName")
    fun updateCategory(currentName: String, newName: String)

    @Query("SELECT EXISTS (SELECT * FROM categories WHERE name == :category)")
    fun categoryExists(category: String): Boolean

}