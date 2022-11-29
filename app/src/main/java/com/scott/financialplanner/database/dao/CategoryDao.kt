package com.scott.financialplanner.database.dao

import androidx.room.*
import com.scott.financialplanner.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun allCategories(): Flow<List<CategoryEntity>>

    @Insert
    fun insertCategory(categoryEntity: CategoryEntity)

    @Query("DELETE FROM categories WHERE name == :name")
    fun deleteCategory(name: String)

    @Query("UPDATE categories SET name = :newName WHERE name == :currentName")
    fun updateCategory(currentName: String, newName: String)

}