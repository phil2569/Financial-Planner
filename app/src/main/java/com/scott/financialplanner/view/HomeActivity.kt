package com.scott.financialplanner.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scott.financialplanner.R
import com.scott.financialplanner.theme.FinancialPlannerTheme
import com.scott.financialplanner.theme.backgroundColor
import com.scott.financialplanner.viewmodel.HomeViewModel
import com.scott.financialplanner.viewmodel.HomeViewModel.CategoryState.Categories
import com.scott.financialplanner.viewmodel.HomeViewModel.CategoryState.Initializing
import com.scott.financialplanner.viewmodel.HomeViewModel.CategoryState.NoCategories
import com.scott.financialplanner.viewmodel.HomeViewModel.UhOh
import com.scott.financialplanner.viewmodel.HomeViewModel.UhOh.CategoryAlreadyExists
import com.scott.financialplanner.viewmodel.HomeViewModel.UhOh.NoUhOh
import dagger.hilt.android.AndroidEntryPoint

/**
 * The Launcher activity.
 * Displays the current expense categories if they exist and allows for the creation of new ones.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancialPlannerTheme {
                HomeScreen(homeViewModel)
                HandleUhOhs(homeViewModel)
            }
        }
    }
}
@Composable
private fun HandleUhOhs(viewModel: HomeViewModel) {
    val uhOh = viewModel.uhOhs.collectAsState().value
    val context = LocalContext.current

    when (uhOh) {
        NoUhOh -> { /* Alright, alright, alright */ }
        is CategoryAlreadyExists -> makeToast(context, "${uhOh.categoryName} Already Exists!")
        UhOh.BlankCategory -> makeToast(context, "Category must not be empty!")
        UhOh.MissingNewExpenseInfo -> makeToast(context, "Description and Amount can't be empty!")
    }
}

@Composable
private fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val categoryLoadingState = viewModel.categoryLoadingState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {

        when (categoryLoadingState) {
            Initializing -> {

            }
            Categories -> {
                TotalExpenses(viewModel = viewModel)

                CategoryRecycler(
                    modifier = Modifier.weight(1f),
                    viewModel = viewModel
                )

                NewCategory(viewModel = viewModel)
            }
            NoCategories -> {
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