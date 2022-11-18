package com.scott.financialplanner.database.repository

import com.scott.financialplanner.*
import com.scott.financialplanner.buildCategory
import com.scott.financialplanner.database.dao.CategoryDao
import com.scott.financialplanner.database.entity.CategoryEntity
import com.scott.financialplanner.toEntity
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryRepositoryImplTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val categoryDao = mockk<CategoryDao>()

    @Before
    fun setup() {
        every { categoryDao.insertCategory(any()) } just Runs
        every { categoryDao.deleteCategory(any()) } just Runs
        every { categoryDao.updateCategory(any(), any()) } just Runs
    }

    @Test
    fun `getAllCategories returns categories from dao`() = runTest {
        // Given
        val category1 = buildCategory(name = "category 1")
        val category2 = buildCategory(name = "category 2")
        val expected = listOf(category1.name, category2.name)

        // When
        every { categoryDao.getCategories() } returns listOf(
            category1.toEntity(),
            category2.toEntity()
        )
        val repository = createRepository()

        // Then
        repository.getAllCategories().shouldBe(expected)
    }

    @Test
    fun `insertCategory inserts into dao`() = runTest {
        // Given
        val entity = CategoryEntity(name = "name")

        // When
        val repository = createRepository()
        repository.insertCategory("name")

        // Then
        verify { categoryDao.insertCategory(entity) }
    }

    @Test
    fun `deleteCategory deletes from dao`() = runTest {
        // When
        val repository = createRepository()
        repository.deleteCategory("name")

        // Then
        verify { categoryDao.deleteCategory("name") }
    }

    @Test
    fun `updateCategoryName updates name in dao`() = runTest {
        // When
        val repository = createRepository()
        repository.updateCategoryName("current","new")

        // Then
        verify { categoryDao.updateCategory("current","new") }
    }

    @Test
    fun `categoryExists checks dao`() = runTest {
        // When
        every { categoryDao.categoryExists("name") } returns true
        val repository = createRepository()

        // Then
        repository.categoryExists("name").shouldBeTrue()
        verify { categoryDao.categoryExists("name") }
    }


    private fun createRepository() = CategoryRepositoryImpl(
        categoryDao
    )
}