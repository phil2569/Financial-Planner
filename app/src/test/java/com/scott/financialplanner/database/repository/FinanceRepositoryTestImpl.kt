package com.scott.financialplanner.database.repository

import app.cash.turbine.test
import com.scott.financialplanner.*
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceRepositoryTestImpl {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val categoryRepository = mockk<CategoryRepository>()
    private val expenseRepository = mockk<ExpenseRepository>()

    @Before
    fun setup() {
        // Prevent from having to mock these in every test
        coEvery { categoryRepository.categoryExists(any()) } returns false
        coEvery { categoryRepository.getAllCategories() } returns emptyList()
        coEvery { categoryRepository.insertCategory(any()) } just Runs
        coEvery { categoryRepository.deleteCategory(any()) } just Runs
        coEvery { categoryRepository.updateCategoryName(any(), any()) } just Runs
        coEvery { expenseRepository.insertExpense(any()) } just Runs
        coEvery { expenseRepository.deleteExpense(any()) } just Runs
        coEvery { expenseRepository.deleteAllCategoryExpenses(any()) } just Runs
        coEvery { expenseRepository.updateCategoryNames(any(), any()) } just Runs
    }

    @Test
    fun `createCategory inserts into the category repository and emits categories`() = runTest {
        // Given
        val category = buildCategory()

        // When
        val repository = createRepository()

        // Then
        repository.categories.test {
            repository.createCategory(category.name)
            coVerify { categoryRepository.insertCategory(category.name) }
            awaitItem().shouldBe(listOf(category))
        }
    }

    @Test
    fun `createExpense inserts expense into expense repository and emits categories`() = runTest {
        // Given
        val category = buildCategory()
        val expense = buildExpense()
        val expectedEmittedCategory = category.copy(
            expenseTotal = category.expenseTotal + expense.amount
        )

        // When
        val repository = createRepository()
        repository.createCategory(category.name)

        // Then
        repository.categories.test {
            repository.createExpense(expense)
            coVerify { expenseRepository.insertExpense(expense) }
            awaitItem().shouldBe(listOf(expectedEmittedCategory))
        }
    }

    @Test
    fun `deleteCategory deletes category from category repository, deletes all expenses, and emits categories`() = runTest {
        // Given
        val category1 = buildCategory("category 1")
        val category2 = buildCategory("category 2")
        val category1Expense1 = buildExpense(associatedCategory = category1.name)

        // When
        val repository = createRepository()
        repository.createCategory(category1.name)
        repository.createExpense(category1Expense1)
        repository.createCategory(category2.name)

        // Then
        repository.categories.test {
            repository.deleteCategory(category1.name)
            coVerify { categoryRepository.deleteCategory(category1.name) }
            coVerify { expenseRepository.deleteAllCategoryExpenses(category1.name) }
            awaitItem().shouldBe(listOf(category2))
        }
    }

    @Test
    fun `deleteExpense deletes category expense and emits categories`() = runTest {
        // Given
        val category = buildCategory()
        val expense1 = buildExpense()
        val expense2 = buildExpense()
        val expectedEmittedCategory = category.copy(
            expenseTotal = category.expenseTotal + expense1.amount
        )

        // When
        val repository = createRepository()
        repository.createCategory(category.name)
        repository.createExpense(expense1)
        repository.createExpense(expense2)

        // Then
        repository.categories.test {
            repository.deleteExpense(expense2)
            coVerify { expenseRepository.deleteExpense(expense2) }
            awaitItem().shouldBe(listOf(expectedEmittedCategory))
        }
    }

    @Test
    fun `editCategory updates expense's category ids, edits category, and emits categories`() = runTest {
        // Given
        val category = buildCategory()
        val expectedEmittedCategory = category.copy(name = "new")

        // When
        val repository = createRepository()
        repository.createCategory(category.name)

        // Then
        repository.categories.test {
            repository.editCategoryName(category.name, "new")
            coVerify { categoryRepository.updateCategoryName(category.name, "new") }
            coVerify { expenseRepository.updateCategoryNames(category.name, "new") }
            awaitItem().shouldBe(listOf(expectedEmittedCategory))
        }
    }

    private fun createRepository() = FinanceRepositoryImpl(
        categoryRepository = categoryRepository,
        expenseRepository = expenseRepository,
        dispatcherRule.testCoroutineProvider
    )
}