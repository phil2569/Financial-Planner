package com.scott.financialplanner.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scott.financialplanner.R
import com.scott.financialplanner.data.Expense
import com.scott.financialplanner.theme.backgroundColor
import com.scott.financialplanner.viewmodel.ExpenseHistoryViewModel
import com.scott.financialplanner.viewmodel.ExpenseHistoryViewModel.ExpenseAction.DeleteExpenseClicked
import com.scott.financialplanner.viewmodel.ExpenseHistoryViewModel.ExpenseHistory.Expenses
import kotlinx.coroutines.channels.SendChannel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpenseHistory(expenseHistoryViewModel: ExpenseHistoryViewModel = viewModel()) {
    val category = expenseHistoryViewModel.categoryName
    val expenseHistory = remember { mutableStateListOf<Expense>() }

    LaunchedEffect(key1 = Unit) {
        expenseHistoryViewModel.expenseHistory.collect {
            when (it) {
                is Expenses -> {
                    expenseHistory.clear()
                    expenseHistory.addAll(it.expenses)
                }
                else -> { /* no op */ }
            }
        }
    }
    val actionChannel = expenseHistoryViewModel.actions

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        TopAppBar {
            Text(
                modifier = Modifier.padding(start = 20.dp),
                style = MaterialTheme.typography.h1,
                text = stringResource(R.string.history_title, category),
            )
        }

        if (expenseHistory.isEmpty()) {
            NoExpenses()
        } else {
            ShowExpenses(
                expenseHistory,
                actionChannel
            )
        }
    }
}

@Composable
private fun NoExpenses() {
    DefaultCard(
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 30.dp)
    ) {
        Text(
            modifier = Modifier.padding(40.dp),
            text = stringResource(id = R.string.history_no_history),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun ShowExpenses(
    expenses: List<Expense>,
    actionChannel: SendChannel<ExpenseHistoryViewModel.ExpenseAction>
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 30.dp),
    ) {
        items(expenses) { expense ->
            ExpenseItem(expense, onClick = {
                actionChannel.trySend(
                    DeleteExpenseClicked(
                        expense
                    )
                )
            })
        }
    }
}

@Composable
private fun ExpenseItem(
    expense: Expense,
    onClick: ((Expense) -> Unit)? = null
) {
    DefaultCard(modifier = Modifier.padding(top = 20.dp)) {
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
                        onClick?.invoke(expense)
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
        ),
    )
}