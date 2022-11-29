package com.scott.financialplanner.database.repository

import com.scott.financialplanner.*
import com.scott.financialplanner.database.dao.CategoryDao
import com.scott.financialplanner.database.dao.ExpenseDao
import com.scott.financialplanner.database.entity.CategoryEntity
import com.scott.financialplanner.database.entity.ExpenseEntity
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceRepositoryImplTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val categoryDao = mockk<CategoryDao>()
    private val expenseDao = mockk<ExpenseDao>()

    @Before
    fun setup() {
        // Prevent from having to mock these in every test
        coEvery { categoryDao.allCategories() } returns flow { emptyList<CategoryEntity>() }
        coEvery { categoryDao.insertCategory(any()) } just Runs
        coEvery { categoryDao.deleteCategory(any()) } just Runs
        coEvery { categoryDao.updateCategory(any(), any()) } just Runs
        coEvery { expenseDao.allExpenses() } returns flow { emptyList<ExpenseEntity>() }
        coEvery { expenseDao.insertExpense(any()) } just Runs
        coEvery { expenseDao.deleteExpense(any()) } just Runs
        coEvery { expenseDao.deleteAllCategoryExpenses(any()) } just Runs
        coEvery { expenseDao.updateCategoryNames(any(), any()) } just Runs
        coEvery { expenseDao.getCategoryExpenses(any()) } returns emptyList()
    }

    @Test
    fun `createCategory inserts into the category dao`() = runTest {
        // Given
        val category = buildCategory().toEntity()

        // When
        createRepository().createCategory(category.name)

        // Then
        coVerify { categoryDao.insertCategory(category) }
    }

    @Test
    fun `createExpense inserts expense into expense dao`() = runTest {
        // Given
        val expense = buildExpense()

        // When
        createRepository().createExpense(expense)

        // Then
        coVerify { expenseDao.insertExpense(expense.toEntity()) }
    }

    @Test
    fun `deleteCategory deletes category from category dao and deletes all expenses associated with it`() = runTest {
        // Given
        val category = buildCategory()

        // When
        createRepository().deleteCategory(category.name)

        // Then
        coVerify { categoryDao.deleteCategory(category.name) }
        coVerify { expenseDao.deleteAllCategoryExpenses(category.name) }
    }

    @Test
    fun `deleteExpense deletes expense from expense dao`() = runTest {
        // Given
        val expense = buildExpense()

        // When
        createRepository().deleteExpense(expense)

        // Then
        coVerify { expenseDao.deleteExpense(expense.dateCreated.timeInMillis) }
    }

    @Test
    fun `editCategory updates category in category and expense dao`() = runTest {
        // Given
        val currentName = "currentName"
        val newName = "newName"

        // When
        createRepository().editCategoryName(currentName, newName)

        // Then
        coVerify { categoryDao.updateCategory(currentName, newName) }
        coVerify { expenseDao.updateCategoryNames(currentName, newName) }
    }

    private fun createRepository() = FinanceRepositoryImpl(
        categoryDao = categoryDao,
        expenseDao = expenseDao,
        dispatcherRule.testCoroutineProvider
    )
}