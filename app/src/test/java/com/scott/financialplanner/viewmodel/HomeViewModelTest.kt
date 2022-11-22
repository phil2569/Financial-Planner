package com.scott.financialplanner.viewmodel

import app.cash.turbine.test
import com.scott.financialplanner.TestDispatcherRule
import com.scott.financialplanner.data.Category
import com.scott.financialplanner.data.CategoryList
import com.scott.financialplanner.database.repository.FinanceRepository
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val financeRepository = mockk<FinanceRepository>()

    private val fakeCategory = Category("fake", 10f)

    @Before
    fun setup() {
        mockRepository(emptyList())
    }

    @Test
    fun `state is NoCategories if repository returns no categories`() = runTest {
        // When
        mockRepository(emptyList())
        val viewmodel = buildViewModel()

        // Then
        viewmodel.categoryLoadingState.test {
            awaitItem().shouldBe(HomeViewModel.CategoryState.NoCategories)
        }
    }

    @Test
    fun `state is Categories if repository returns categories`() = runTest {
        // When
        mockRepository(listOf(fakeCategory))
        val viewmodel = buildViewModel()

        // Then
        viewmodel.categoryLoadingState.test {
            awaitItem().shouldBe(HomeViewModel.CategoryState.Categories)
        }
    }

    @Test
    fun `emits CategoryList if repository returns categories`() = runTest {
        // When
        mockRepository(listOf(fakeCategory))
        val viewmodel = buildViewModel()

        // Then
        viewmodel.categories.test {
            awaitItem().shouldBe(CategoryList(listOf(fakeCategory)))
        }
    }

    /*@Test
    fun `showNewCategoryInput should be true when new category is clicked`() = runTest {
        // Given
        val viewmodel = buildViewModel()

        // When
        viewmodel.actions.send(HomeViewModel.HomeScreenAction.NewCategoryClicked)

        // Then
        viewmodel.homeScreenUiState.test {
            awaitItem().apply {
                showNewCategoryInput.shouldBeTrue()
            }
        }
    }

    @Test
    fun `showNewCategoryInput should be false when accept new category is clicked`() = runTest {
        // Given
        val viewmodel = buildViewModel()

        // When
        viewmodel.actions.send(HomeViewModel.HomeScreenAction.CreateNewCategory(""))

        // Then
        viewmodel.homeScreenUiState.test {
            awaitItem().apply {
                showNewCategoryInput.shouldBeFalse()
            }
        }
    }

    @Test
    fun `showNewCategoryInput should be false when cancel new category is clicked`() = runTest {
        // Given
        val viewmodel = buildViewModel()

        // When
        viewmodel.actions.send(HomeViewModel.HomeScreenAction.CancelNewCategoryClicked)

        // Then
        viewmodel.homeScreenUiState.test {
            awaitItem().apply {
                showNewCategoryInput.shouldBeFalse()
            }
        }
    }*/

    private fun mockRepository(categories: List<Category> = emptyList()) {
        val categoryList = MutableStateFlow(CategoryList(categories)).asStateFlow()
        every { financeRepository.categories } returns categoryList
    }

    private fun buildViewModel() = HomeViewModel(
        financeRepository = financeRepository,
        dispatcherProvider = dispatcherRule.testCoroutineProvider
    )
}