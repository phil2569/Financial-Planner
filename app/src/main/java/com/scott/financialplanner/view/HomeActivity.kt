package com.scott.financialplanner.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scott.financialplanner.R
import com.scott.financialplanner.theme.FinancialPlannerTheme
import com.scott.financialplanner.theme.backgroundColor
import com.scott.financialplanner.viewmodel.HomeViewModel
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
            }
        }
    }
}

@Composable
private fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState = viewModel.homeScreenUiState.collectAsState(initial = HomeViewModel.HomeScreenUiState()).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {

        if (uiState.showCategories) {
            TotalExpenses(viewModel = viewModel)
            CategoryRecycler(
                modifier = Modifier.weight(1f),
                viewModel = viewModel
            )
        } else {
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
        }

        NewCategory(viewModel = viewModel)
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeActivityPreview() {
    HomeScreen()
}