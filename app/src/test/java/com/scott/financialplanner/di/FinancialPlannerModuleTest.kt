package com.scott.financialplanner.di

import com.scott.financialplanner.TestDispatcherRule
import com.scott.financialplanner.database.FinanceDatabase
import com.scott.financialplanner.database.repository.FinanceRepositoryImpl
import com.scott.financialplanner.provider.DispatchProviderImpl
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.mockk
import org.junit.Test

class FinancialPlannerModuleTest {

    @Test
    fun `provideDatabase returns FinanceDatabase`() {
        // Given
        val module = FinancialPlannerModule

        // When
        val result = module.provideDatabase(mockk())

        // Then
        result.shouldBeInstanceOf<FinanceDatabase>()
    }

    @Test
    fun `provideFinanceRepository returns FinanceRepositoryImpl`() {
        // Given
        val module = FinancialPlannerModule

        // When
        val result = module.provideFinanceRepository(
            mockk(relaxed = true),
            TestDispatcherRule().testCoroutineProvider
        )

        // Then
        result.shouldBeInstanceOf<FinanceRepositoryImpl>()
    }

    @Test
    fun `provideDispatchProvider returns DispatchProviderImpl`() {
        // Given
        val module = FinancialPlannerModule

        // When
        val result = module.provideDispatchProvider()

        // Then
        result.shouldBeInstanceOf<DispatchProviderImpl>()
    }
}