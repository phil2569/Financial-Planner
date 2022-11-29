package com.scott.financialplanner.database

import android.os.Build.VERSION
import androidx.room.Database
import androidx.room.RoomDatabase
import com.scott.financialplanner.database.dao.CategoryDao
import com.scott.financialplanner.database.dao.ExpenseDao
import com.scott.financialplanner.database.entity.CategoryEntity
import com.scott.financialplanner.database.entity.ExpenseEntity

@Database(
    entities = [CategoryEntity::class, ExpenseEntity::class],
    version = FinanceDatabase.VERSION
)
internal abstract class FinanceDatabase : RoomDatabase() {

    companion object {
        const val VERSION = 1
        const val NAME = "financial_planner_database"
    }

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

}