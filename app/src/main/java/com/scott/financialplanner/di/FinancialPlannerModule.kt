package com.scott.financialplanner.di

import android.content.Context
import androidx.room.Room
import com.scott.financialplanner.database.FinanceDatabase
import com.scott.financialplanner.database.repository.CategoryRepository
import com.scott.financialplanner.database.repository.CategoryRepositoryImpl
import com.scott.financialplanner.database.repository.ExpenseRepository
import com.scott.financialplanner.database.repository.ExpenseRepositoryImpl
import com.scott.financialplanner.database.repository.FinanceRepository
import com.scott.financialplanner.database.repository.FinanceRepositoryImpl
import com.scott.financialplanner.provider.DispatchProviderImpl
import com.scott.financialplanner.provider.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FinancialPlannerModule {

    @Provides
    @Singleton
    internal fun provideDatabase(@ApplicationContext context: Context): FinanceDatabase =
        Room.databaseBuilder(
            context,
            FinanceDatabase::class.java, "financial_planner_database"
        ).build()

    @Provides
    @Singleton
    internal fun provideFinanceRepository(
        categoryRepository: CategoryRepository,
        expenseRepository: ExpenseRepository,
        dispatcherProvider: DispatcherProvider
    ): FinanceRepository = FinanceRepositoryImpl(
        categoryRepository,
        expenseRepository,
        dispatcherProvider
    )

    @Provides
    @Singleton
    internal fun provideCategoryRepository(
        database: FinanceDatabase
    ): CategoryRepository = CategoryRepositoryImpl(database.categoryDao())

    @Provides
    @Singleton
    internal fun provideExpenseRepository(
        database: FinanceDatabase
    ): ExpenseRepository = ExpenseRepositoryImpl(database.expenseDao())

    @Provides
    @Singleton
    internal fun provideDispatchProvider(): DispatcherProvider = DispatchProviderImpl()

}