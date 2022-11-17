package com.scott.financialplanner

import app.cash.turbine.test
import com.scott.financialplanner.viewmodel.HomeViewModel
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Test
    fun `home screen initializes with correct state`() = runTest {
        // When
        val viewmodel = buildViewModel()

        // Then
        viewmodel.homeScreenState.test {
            awaitItem().apply {
                showNewCategoryInput.shouldBeFalse()
            }
        }
    }

    @Test
    fun `showNewCategoryInput should be true when new category is clicked`() = runTest {
        // Given
        val viewmodel = buildViewModel()

        // When
        viewmodel.actions.send(HomeViewModel.HomeScreenAction.NewCategoryClicked)

        // Then
        viewmodel.homeScreenState.test {
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
        viewmodel.actions.send(HomeViewModel.HomeScreenAction.AcceptNewCategoryClicked(""))

        // Then
        viewmodel.homeScreenState.test {
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
        viewmodel.homeScreenState.test {
            awaitItem().apply {
                showNewCategoryInput.shouldBeFalse()
            }
        }
    }

    private fun buildViewModel() = HomeViewModel()
}