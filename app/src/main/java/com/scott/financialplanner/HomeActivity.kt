package com.scott.financialplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scott.financialplanner.compose.FinancialPlannerTheme
import com.scott.financialplanner.compose.backgroundColor
import com.scott.financialplanner.view.NewCategory
import com.scott.financialplanner.viewmodel.HomeViewModel

/**
 * The Launcher activity.
 * Displays the current expense categories if they exist and allows for the creation of new ones.
 */
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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        val (newCategoryButton, babyKev) = createRefs()

        NewCategory(
            viewModel = viewModel,
            modifier = Modifier
                .constrainAs(newCategoryButton) {
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    HomeScreen()
}