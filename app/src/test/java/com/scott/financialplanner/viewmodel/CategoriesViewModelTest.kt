package com.scott.financialplanner.viewmodel

import app.cash.turbine.test
import com.scott.financialplanner.TestDispatcherRule
import com.scott.financialplanner.buildCategory
import com.scott.financialplanner.buildExpense
import com.scott.financialplanner.data.Category
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.database.repository.FinanceRepository
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryAction.DeleteCategoryClicked
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryAction.ExpenseHistoryClicked
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryAction.NewCategoryClicked
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryAction.SaveNewExpenseClicked
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryAction.UpdateCategoryClicked
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryEvent.CategoryAlreadyExists
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryEvent.NavigateToExpenseHistory
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

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
    }

    @Test
    fun `state is Initializing before financeRepository emits categories`() = runTest {
        // When
        val viewmodel = buildViewModel()

        // Then
        viewmodel.categoryLoadingState.test {
            awaitItem().shouldBe(CategoriesViewModel.LoadingState.Initializing)
        }
    }

    @Test
    fun `state is Initialized after financeRepository emits categories`() = runTest {
        // When
        val viewmodel = buildViewModel()
        financeRepositoryCategories.emit(emptyMap())

        // Then
        viewmodel.categoryLoadingState.test {
            awaitItem().shouldBe(CategoriesViewModel.LoadingState.Initialized)
        }
    }

    @Test
    fun `updates total expenses when financeRepository emits categories`() = runTest {
        // When
        val viewmodel = buildViewModel()
        financeRepositoryCategories.emit(categoryMap)

        // Then
        viewmodel.totalMonthlyExpenses.test {
            awaitItem().shouldBe(30f)
        }
    }

    @Test
    fun `emits categories when financeRepository emits categories`() = runTest {
        // Given
        val expectedCategories = listOf(Category(fakeCategory.name, 30f))

        // When
        val viewmodel = buildViewModel()

        // Then
        viewmodel.categories.test {
            financeRepositoryCategories.emit(categoryMap)
            awaitItem().shouldBe(expectedCategories)
        }
    }

    @Test
    fun `NewCategoryClicked creates new category if it doesn't exist`() = runTest {
        // Given
        every { financeRepository.categoryExists(fakeCategory.name) } returns false

        // When
        buildViewModel().actions.send(NewCategoryClicked(fakeCategory.name))

        // Then
        verify { financeRepository.createCategory(fakeCategory.name) }
    }

    @Test
    fun `NewCategoryClicked sends event if it doesn't exist`() = runTest {
        // Given
        every { financeRepository.categoryExists(fakeCategory.name) } returns true

        // When
        val viewModel = buildViewModel()

        // Then
        viewModel.events.test {
            viewModel.actions.send(NewCategoryClicked(fakeCategory.name))
            verify(inverse = true) { financeRepository.createCategory(fakeCategory.name) }
            awaitItem().shouldBe(CategoryAlreadyExists(fakeCategory.name))
        }
    }

    @Test
    fun `SaveNewExpenseClicked creates expense`() = runTest {
        // When
        buildViewModel().actions.send(
            SaveNewExpenseClicked(
                fakeExpense1.associatedCategory,
                fakeExpense1.description,
                fakeExpense1.amount.toString(),
                fakeExpense1.dateCreated
            )
        )

        // Then
        verify { financeRepository.createExpense(fakeExpense1) }
    }

    @Test
    fun `DeleteCategoryClicked deletes category`() = runTest {
        // When
        buildViewModel().actions.send(DeleteCategoryClicked(fakeCategory.name))

        // Then
        verify { financeRepository.deleteCategory(fakeCategory.name) }
    }

    @Test
    fun `UpdateCategoryClicked edits category`() = runTest {
        // When
        buildViewModel().actions.send(UpdateCategoryClicked(fakeCategory.name, "newName"))

        // Then
        verify { financeRepository.editCategoryName(fakeCategory.name, "newName") }
    }

    @Test
    fun `ExpenseHistoryClicked sends navigate event`() = runTest {
        // When
        val viewmodel = buildViewModel()

        // Then
        viewmodel.events.test {
            viewmodel.actions.send(ExpenseHistoryClicked(fakeCategory.name))
            awaitItem().shouldBe(NavigateToExpenseHistory(fakeCategory.name))
        }
    }

    private fun buildViewModel() = CategoriesViewModel(
        financeRepository = financeRepository,
        dispatcherProvider = dispatcherRule.testCoroutineProvider
    )
}