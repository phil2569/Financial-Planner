package com.scott.financialplanner.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
internal data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "amount") val amount: Float,
    @ColumnInfo(name = "date_created_millis") val dateCreatedMillis: Long,
    // The category name associated with this expense.
    @ColumnInfo(name = "associated_category") val associatedCategory: String,
)