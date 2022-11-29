package com.scott.financialplanner.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.scott.financialplanner.TestDispatcherRule
import com.scott.financialplanner.buildCategory
import com.scott.financialplanner.buildExpense
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.repository.FinanceRepository
import com.scott.financialplanner.viewmodel.ExpenseHistoryViewModel.ExpenseAction.DeleteExpenseClicked
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseHistoryViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val financeRepository = mockk<FinanceRepository>()
    private val financeRepositoryCategories = MutableSharedFlow<Map<String, List<Expense>>>()

    private val fakeCategory = buildCategory("fakeCategory", 10f)
    private val fakeExpense1 = buildExpense(description = "exp1", associatedCategory = fakeCategory.name, amount = 10f)
    private val fakeExpense2 = buildExpense(description = "exp2", associatedCategory = fakeCategory.name, amount = 20f)

    private val categoryMap = mapOf(fakeCategory.name to listOf(fakeExpense1, fakeExpense2))

    @Before
    fun setup() {
        every { financeRepository.categories } returns financeRepositoryCategories.asSharedFlow()
        every { financeRepository.getCategoryExpenses(fakeCategory.name) } returns listOf(fakeExpense1)
    }

    @Test
    fun `refreshes expenses on init`() = runTest {
        // When
        val viewmodel = buildViewModel()

        // Then
        viewmodel.expenseHistory.test {
            verify { financeRepository.getCategoryExpenses(fakeCategory.name) }
            awaitItem().shouldBe(listOf(fakeExpense1))
        }
    }

    @Test
    fun `refreshes expenses when financeRepository emits categories`() = runTest {
        // When
        val viewmodel = buildViewModel()
        financeRepositoryCategories.emit(categoryMap)

        // Then
        viewmodel.expenseHistory.test {
            verify { financeRepository.getCategoryExpenses(fakeCategory.name) }
            awaitItem().shouldBe(listOf(fakeExpense2, fakeExpense1))
        }
    }

    @Test
    fun `DeleteExpenseClicked deletes expense`() = runTest {
        // When
        buildViewModel().actions.send(DeleteExpenseClicked(fakeExpense1))

        // Then
        verify { financeRepository.deleteExpense(fakeExpense1) }
    }

    private fun buildViewModel(): ExpenseHistoryViewModel {
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["category_extra"] = fakeCategory.name
        return ExpenseHistoryViewModel(
            financeRepository,
            savedStateHandle
        )
    }
}