package com.scott.financialplanner.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scott.financialplanner.R
import com.scott.financialplanner.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun TotalExpenses(
    viewModel: HomeViewModel = viewModel()) {
    val totalSpent = viewModel.totalMonthlyExpenses.collectAsState().value

    DefaultCard {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp)
        ) {
            val (text, amount) = createRefs()

            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(text) {
                        start.linkTo(parent.start)
                    },
                style = MaterialTheme.typography.h2,
                text = stringResource(id = R.string.home_total_monthly_expenses)
            )

            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(amount) {
                        end.linkTo(parent.end)
                    },
                style = MaterialTheme.typography.h2,
                text = NumberFormat.getCurrencyInstance(Locale.US).format(totalSpent)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TotalExpensesPreview() {
    TotalExpenses()
}