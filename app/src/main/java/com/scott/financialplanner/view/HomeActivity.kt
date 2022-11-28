package com.scott.financialplanner.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scott.financialplanner.R
import com.scott.financialplanner.data.Category
import com.scott.financialplanner.theme.FinancialPlannerTheme
import com.scott.financialplanner.theme.backgroundColor
import com.scott.financialplanner.viewmodel.CategoriesViewModel
import com.scott.financialplanner.viewmodel.CategoriesViewModel.LoadingState.Initialized
import com.scott.financialplanner.viewmodel.CategoriesViewModel.LoadingState.Initializing
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryEvent.CategoryAlreadyExists
import com.scott.financialplanner.viewmodel.CategoriesViewModel.CategoryEvent.NavigateToExpenseHistory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * The Launcher activity.
 * Displays the current expense categories if they exist and allows for the creation of new ones.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val homeViewModel by viewModels<CategoriesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancialPlannerTheme {
                HomeScreen(homeViewModel)
                ConsumeEvents(homeViewModel)
            }
        }
    }
}

@Composable
private fun ConsumeEvents(
    viewModel: CategoriesViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect {
            when (it) {
                is CategoryAlreadyExists -> makeToast(context, "${it.categoryName} Already Exists!")
                is NavigateToExpenseHistory -> {
                    val intent = Intent(context, ExpenseHistoryActivity::class.java).apply {
                        putExtra("category_extra", it.categoryName)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(viewModel: CategoriesViewModel = viewModel()) {
    val categoryLoadingState = viewModel.categoryLoadingState.collectAsState().value
    val categories = remember { mutableStateListOf<Category>() }
    println("testingg home screen")

    LaunchedEffect(Unit) {
        viewModel.categories.collect {
            categories.clear()
            categories.addAll(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {

        when (categoryLoadingState) {
            Initializing -> {
                // TODO
            }
            Initialized -> {
                if (categories.isEmpty()) {
                    TopAppBar(backgroundColor = MaterialTheme.colors.primary) {
                        Text(
                            modifier = Modifier.padding(start = 12.dp),
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.h1
                        )
                    }

                    BabyKev(
                        modifier = Modifier.weight(1f)
                    )

                    NewCategory(viewModel = viewModel)
                } else {
                    TotalExpenses(viewModel = viewModel)

                    CategoryRecycler(
                        modifier = Modifier.weight(1f),
                        categories = categories,
                        homeScreenActions = viewModel.actions
                    )

                    NewCategory(viewModel = viewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeActivityPreview() {
    HomeScreen()
}

private fun makeToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}