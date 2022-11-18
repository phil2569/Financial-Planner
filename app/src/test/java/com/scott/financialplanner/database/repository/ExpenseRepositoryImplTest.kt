package com.scott.financialplanner.database.repository

import com.scott.financialplanner.*
import com.scott.financialplanner.database.dao.ExpenseDao
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseRepositoryImplTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val expenseDao = mockk<ExpenseDao>()

    @Before
    fun setup() {
        every { expenseDao.deleteExpense(any()) } just Runs
        every { expenseDao.deleteAllCategoryExpenses(any()) } just Runs
        every { expenseDao.updateCategoryNames(any(), any()) } just Runs
        every { expenseDao.insertExpense(any()) } just Runs
    }

    @Test
    fun `getExpenseTotals returns sum of expenses in dao`() = runTest {
        // Given
        val expense1 = buildExpense(amount = 10f)
        val expense2 = buildExpense(amount = 10f)
        val expenses = listOf(
            expense1.toEntity(),
            expense2.toEntity()
        )

        // When
        every { expenseDao.getAllExpenses() } returns expenses
        val repository = createRepository()

        // Then
        repository.getExpenseTotals().shouldBe(20f)
    }

    @Test
    fun `getCategoryExpenses returns dao expenses`() = runTest {
        // Given
        val category = buildCategory(name = "name")
        val expense1 = buildExpense(associatedCategory = category.name)
        val expense2 = buildExpense(associatedCategory = category.name)
        val expenses = listOf(
            expense1.toEntity(),
            expense2.toEntity()
        )

        // When
        every { expenseDao.getCategoryExpenses(category.name) } returns expenses
        val repository = createRepository()

        // Then
        repository.getCategoryExpenses(category.name).shouldBe(listOf(expense1, expense2))
    }

    @Test
    fun `deleteExpense deletes from dao`() = runTest {
        // Given
        val expense = buildExpense()

        // When
        val repository = createRepository()
        repository.deleteExpense(expense)

        // Then
        verify { expenseDao.deleteExpense(expense.dateCreated.timeInMillis) }
    }

    @Test
    fun `deleteAllCategoryExpenses deletes from dao`() = runTest {
        // When
        val repository = createRepository()
        repository.deleteAllCategoryExpenses("name")

        // Then
        verify { expenseDao.deleteAllCategoryExpenses("name") }
    }

    @Test
    fun `updateCategoryNames updates names in dao`() = runTest {
        // When
        val repository = createRepository()
        repository.updateCategoryNames("current", "new")

        // Then
        verify { expenseDao.updateCategoryNames("current", "new") }
    }

    @Test
    fun `insertExpense inserts into dao`() = runTest {
        // Given
        val expense = buildExpense()

        // When
        val repository = createRepository()
        repository.insertExpense(expense)

        // Then
        verify { expenseDao.insertExpense(expense.toEntity()) }
    }

    private fun createRepository() = ExpenseRepositoryImpl(
        expenseDao
    )
}