package com.scott.financialplanner.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.scott.financialplanner.R
import com.scott.financialplanner.data.Category
import com.scott.financialplanner.viewmodel.HomeViewModel
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.CreateExpense
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.DeleteCategory
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.UpdateCategoryName
import kotlinx.coroutines.channels.SendChannel
import java.text.NumberFormat
import java.util.*

@Composable
fun CategoryRecycler(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    homeScreenActions: SendChannel<HomeViewModel.HomeScreenAction>
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 30.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                deleteCategoryListener = { category ->
                    homeScreenActions.trySend(DeleteCategory(category))
                },
                updateCategoryListener = { currentName, newName ->
                    homeScreenActions.trySend(
                        UpdateCategoryName(
                            currentName = currentName,
                            newName = newName
                        )
                    )
                },
                historyListener = {
                    //viewModel.actions.trySend(His) todo
                },
                addExpenseListener = { description, category, amount ->
                    homeScreenActions.trySend(
                        CreateExpense(
                            associatedCategory = category,
                            description = description,
                            amount = amount
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    deleteCategoryListener: ((String) -> Unit)? = null,
    updateCategoryListener: ((String, String) -> Unit)? = null,
    historyListener: ((String) -> Unit)? = null,
    addExpenseListener: ((String, String, String) -> Unit)? = null
) {
    DefaultCard(modifier = Modifier.padding(bottom = 30.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp)
        ) {

            CategoryContent(
                category = category,
                deleteCategoryListener = deleteCategoryListener,
                updateCategoryListener = updateCategoryListener
            )
            ExpenseContent(
                category = category,
                addExpenseListener = addExpenseListener,
                historyListener = historyListener
            )
        }
    }
}

@Composable
fun CategoryContent(
    category: Category,
    deleteCategoryListener: ((String) -> Unit)? = null,
    updateCategoryListener: ((String, String) -> Unit)? = null,
) {
    var categoryName by remember { mutableStateOf(category.name) }
    var editModeEnabled by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // reset the value. I don't actually want to remember it, but compose complains about using
    // mutableStateOf without remember.
    categoryName = category.name

    LaunchedEffect(editModeEnabled) {
        if (editModeEnabled) {
            focusRequester.requestFocus()
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (header, totalText, totalValue) = createRefs()

        Row(
            modifier = Modifier.constrainAs(header) {},
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (!editModeEnabled) {
                Image(
                    modifier = Modifier
                        .clickable {
                            deleteCategoryListener?.invoke(category.name)
                        }
                        .width(24.dp)
                        .height(24.dp),
                    painter = painterResource(
                        id = R.drawable.ic_delete
                    ), contentDescription = null
                )
            }

            BasicTextField(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f)
                    .padding(start = 10.dp, end = 10.dp)
                    .focusRequester(focusRequester),
                value = categoryName,
                onValueChange = {
                    categoryName = it
                },
                textStyle = MaterialTheme.typography.h3.copy(textAlign = TextAlign.Center),
                enabled = editModeEnabled,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        editModeEnabled = false
                        updateCategoryListener?.invoke(
                            category.name,
                            categoryName
                        )
                    }
                )
            )

            Image(
                modifier = Modifier
                    .clickable {
                        editModeEnabled = !editModeEnabled
                    }
                    .width(24.dp)
                    .height(24.dp),
                painter = painterResource(
                    id = if (editModeEnabled) R.drawable.ic_cancel else R.drawable.ic_edit
                ), contentDescription = null
            )
        }

        Text(
            modifier = Modifier
                .constrainAs(totalText) {
                    top.linkTo(header.bottom)
                    start.linkTo(parent.start)
                }
                .padding(top = 30.dp),
            text = stringResource(id = R.string.home_adapter_total),
            style = MaterialTheme.typography.body1
        )

        Text(
            modifier = Modifier
                .constrainAs(totalValue) {
                    top.linkTo(totalText.top)
                    end.linkTo(parent.end)
                }
                .padding(top = 30.dp),
            text = NumberFormat.getCurrencyInstance(Locale.US).format(category.expenseTotal),
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun ExpenseContent(
    category: Category,
    historyListener: ((String) -> Unit)? = null,
    addExpenseListener: ((String, String, String) -> Unit)? = null
) {
    val context = LocalContext.current
    var showNewExpense by remember { mutableStateOf(false) }
    var newExpenseDescription by remember { mutableStateOf("") }
    var newExpenseAmount by remember { mutableStateOf("") }
    val emptyContentMessage = stringResource(id = R.string.home_empty_description_or_price)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 30.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                modifier = Modifier
                    .clickable {
                        historyListener?.invoke(category.name)
                    },
                painter = painterResource(id = R.drawable.ic_history), contentDescription = null
            )

            Image(
                modifier = Modifier
                    .clickable {
                        showNewExpense = true
                    },
                painter = painterResource(id = R.drawable.ic_add_circle),
                contentDescription = null
            )
        }

        // New expense container
        if (showNewExpense) {
            val closeNewExpense = {
                showNewExpense = false
                newExpenseDescription = ""
                newExpenseAmount = ""
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 20.dp)
            ) {
                Column {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = newExpenseDescription,
                        onValueChange = {
                            newExpenseDescription = it
                        },
                        label = { Text(stringResource(id = R.string.home_adapter_description)) }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = newExpenseAmount,
                        onValueChange = {
                            newExpenseAmount = it
                        },
                        label = { Text(stringResource(id = R.string.home_adapter_amount)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SecondaryButton(
                        text = stringResource(id = R.string.home_adapter_cancel_button),
                        onClick = {
                            closeNewExpense.invoke()
                        }
                    )

                    PrimaryButton(
                        text = stringResource(id = R.string.home_adapter_save_button),
                        onClick = {
                            if (newExpenseDescription.isEmpty() ||
                                    newExpenseAmount.isEmpty()) {
                                Toast.makeText(context, emptyContentMessage, Toast.LENGTH_SHORT).show()
                            } else {
                                addExpenseListener?.invoke(
                                    newExpenseDescription,
                                    category.name,
                                    newExpenseAmount
                                )
                                closeNewExpense.invoke()
                            }
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CategoryCardPreview() {
    CategoryCard(category = Category("Category", 2f))
}