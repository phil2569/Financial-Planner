package com.scott.financialplanner.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scott.financialplanner.database.dao.CategoryDao
import com.scott.financialplanner.database.dao.ExpenseDao
import com.scott.financialplanner.database.entity.CategoryEntity
import com.scott.financialplanner.database.entity.ExpenseEntity

@Database(entities = [CategoryEntity::class, ExpenseEntity::class], version = 1)
internal abstract class FinanceDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

}