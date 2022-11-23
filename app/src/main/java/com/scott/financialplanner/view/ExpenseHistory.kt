package com.scott.financialplanner.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scott.financialplanner.R
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.viewmodel.ExpenseHistoryViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.exp

@Composable
fun ExpenseHistory(expenseHistoryViewModel: ExpenseHistoryViewModel = viewModel()) {
    val expenses = expenseHistoryViewModel.expenses.collectAsState().value
    println("testingg expenses: $expenses")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        items(expenses) { expense ->
            ExpenseItem(expense)
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    DefaultCard {
        ConstraintLayout(modifier = Modifier.padding(20.dp)) {
            val (date, delete, description, amount) = createRefs()

            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(date) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                text = formateDate(expense.dateCreated.timeInMillis),
                style = MaterialTheme.typography.h3
            )

            Image(
                modifier = Modifier
                    .constrainAs(delete) {
                        top.linkTo(date.top)
                        end.linkTo(parent.end)
                    }
                    .clickable {

                    },
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = null,
            )

            Text(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .wrapContentSize()
                    .constrainAs(description) {
                        top.linkTo(date.bottom)
                        start.linkTo(parent.start)
                    },
                text = expense.description,
                style = MaterialTheme.typography.body1
            )

            Text(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .wrapContentSize()
                    .constrainAs(amount) {
                        top.linkTo(date.bottom)
                        end.linkTo(parent.end)
                    },
                text = NumberFormat.getCurrencyInstance(Locale.US).format(expense.amount),
                style = MaterialTheme.typography.body1
            )
        }
    }
}

private fun formateDate(dateInMillis: Long): String =
    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(dateInMillis)

@Preview
@Composable
fun ExpenseHistoryPreview() {
    ExpenseItem(
        expense = Expense(
            "description",
            amount = 10f,
            dateCreated = Calendar.getInstance(),
            "Category"
        )
    )
}