package com.scott.financialplanner.view

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.unit.IntOffset
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
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 30.dp),
        state = listState
    ) {
        itemsIndexed(categories) { index, category ->
            CategoryCard(
                category = category,
                state = listState,
                index = index,
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
    state: LazyListState,
    index: Int,
    deleteCategoryListener: ((String) -> Unit)? = null,
    updateCategoryListener: ((String, String) -> Unit)? = null,
    historyListener: ((String) -> Unit)? = null,
    addExpenseListener: ((String, String, String) -> Unit)? = null
) {
    ConstraintLayout {
        val showNewExpense = remember { mutableStateOf(false) }

        val (mainContent, expenseContent) = createRefs()

        AnimatedVisibility(
            showNewExpense.value,
            modifier = Modifier.constrainAs(expenseContent) {
                top.linkTo(mainContent.bottom)
            },
            enter = slideIn(tween(500, easing = LinearOutSlowInEasing)) { fullSize ->
                IntOffset(0, -fullSize.height)
            } + fadeIn(initialAlpha = 0f),
            exit = slideOut(tween(500, easing = LinearOutSlowInEasing)) { fullSize ->
                IntOffset(0, -fullSize.height)
            } + fadeOut(),
        ) {
            LaunchedEffect(key1 = Unit) {
                state.animateScrollToItem(index)
            }
            DefaultCard {
                NewExpense(
                    category = category,
                    showNewExpense = showNewExpense,
                    addExpenseListener = addExpenseListener
                )
            }
        }

        val elevation = if (showNewExpense.value) 0.dp else 6.dp
        DefaultCard(
            modifier = Modifier
                .padding(top = 30.dp)
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(expenseContent.top)
                }, elevation = elevation
        ) {
            MainContent(
                category = category,
                showNewExpense = showNewExpense,
                deleteCategoryListener = deleteCategoryListener,
                updateCategoryListener = updateCategoryListener,
                historyListener = historyListener
            )
        }
    }
}

@Composable
fun MainContent(
    category: Category,
    showNewExpense: MutableState<Boolean>,
    deleteCategoryListener: ((String) -> Unit)? = null,
    updateCategoryListener: ((String, String) -> Unit)? = null,
    historyListener: ((String) -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        Header(
            categoryName = category.name,
            deleteCategoryListener = deleteCategoryListener,
            updateCategoryListener = updateCategoryListener
        )

        CategoryTotal(
            modifier = Modifier.padding(top = 30.dp, start = 30.dp, end = 30.dp),
            categoryTotal = category.expenseTotal
        )

        ExpenseButtons(
            modifier = Modifier.padding(top = 30.dp),
            categoryName = category.name,
            historyListener = historyListener,
            showNewExpense = showNewExpense
        )
    }
}

/**
 * Represents the top row in the card.
 * Contains the delete button, category name, and edit button.
 */
@Composable
fun Header(
    categoryName: String,
    deleteCategoryListener: ((String) -> Unit)? = null,
    updateCategoryListener: ((String, String) -> Unit)? = null,
) {
    val context = LocalContext.current
    var editModeEnabled by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (!editModeEnabled) {
            Image(
                modifier = Modifier
                    .clickable {
                        deleteCategoryListener?.invoke(categoryName)
                    }
                    .width(24.dp)
                    .height(24.dp),
                painter = painterResource(
                    id = R.drawable.ic_delete
                ), contentDescription = null
            )
        }

        val categoryTitleModifier = Modifier
            .wrapContentSize()
            .weight(1f)
            .padding(start = 10.dp, end = 10.dp)
        val categoryTitleStyle = MaterialTheme.typography.h3.copy(textAlign = TextAlign.Center)
        if (editModeEnabled) {
            var updatedCategoryName by remember { mutableStateOf(categoryName) }
            val focusRequester = FocusRequester()
            val emptyCategoryMessage = stringResource(id = R.string.home_empty_category)
            BasicTextField(
                modifier = categoryTitleModifier.focusRequester(focusRequester),
                value = updatedCategoryName,
                onValueChange = {
                    updatedCategoryName = it
                },
                textStyle = categoryTitleStyle,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (updatedCategoryName.isEmpty()) {
                            Toast.makeText(context, emptyCategoryMessage, Toast.LENGTH_SHORT).show()
                        } else {
                            editModeEnabled = false
                            updateCategoryListener?.invoke(
                                categoryName,
                                updatedCategoryName
                            )
                        }
                    }
                )
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        } else {
            Text(
                modifier = categoryTitleModifier,
                text = categoryName,
                style = categoryTitleStyle
            )
        }

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
}

/**
 * Represents the second row in the card.
 * Displays the category expense total.
 */
@Composable
fun CategoryTotal(
    categoryTotal: Float,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier.fillMaxWidth()) {
        val (totalText, totalValue) = createRefs()
        Text(
            modifier = Modifier.constrainAs(totalText) {
                start.linkTo(parent.start)
            },
            text = stringResource(id = R.string.home_adapter_total),
            style = MaterialTheme.typography.body1
        )

        Text(
            modifier = Modifier.constrainAs(totalValue) {
                end.linkTo(parent.end)
            },
            text = NumberFormat.getCurrencyInstance(Locale.US).format(categoryTotal),
            style = MaterialTheme.typography.body1
        )
    }
}

/**
 * Represents the 3rd row in the card.
 * Contains the history and add expense buttons.
 */
@Composable
fun ExpenseButtons(
    modifier: Modifier = Modifier,
    categoryName: String,
    showNewExpense: MutableState<Boolean>,
    historyListener: ((String) -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            modifier = Modifier
                .clickable {
                    historyListener?.invoke(categoryName)
                },
            painter = painterResource(id = R.drawable.ic_history), contentDescription = null
        )

        Image(
            modifier = Modifier
                .clickable {
                    showNewExpense.value = true
                },
            painter = painterResource(id = R.drawable.ic_add_circle),
            contentDescription = null
        )
    }
}

/**
 * The new expense container.
 * Hides behind the MainContent until the new expense button is pressed.
 */
@Composable
fun NewExpense(
    category: Category,
    showNewExpense: MutableState<Boolean>,
    addExpenseListener: ((String, String, String) -> Unit)? = null
) {
    val context = LocalContext.current
    var newExpenseDescription by remember { mutableStateOf("") }
    var newExpenseAmount by remember { mutableStateOf("") }
    val emptyContentMessage = stringResource(id = R.string.home_empty_description_or_price)

    Column(
        modifier = Modifier
            .padding(top = 0.dp, start = 30.dp, end = 30.dp, bottom = 30.dp)
            .fillMaxWidth()
    ) {

        val closeNewExpense = {
            showNewExpense.value = false
            newExpenseDescription = ""
            newExpenseAmount = ""
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = newExpenseDescription,
            onValueChange = {
                newExpenseDescription = it
            },
            label = { Text(stringResource(id = R.string.home_adapter_description)) }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = newExpenseAmount,
            onValueChange = {
                // Verify it's a float.
                // KeyboardType.Decimal still allows multiple dots (Dumb, I know).
                if (!it.contains(".*\\..*\\..*".toRegex())) {
                    newExpenseAmount = it
                }
            },
            label = { Text(stringResource(id = R.string.home_adapter_amount)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                        newExpenseAmount.isEmpty()
                    ) {
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


@Preview(showBackground = true)
@Composable
fun CategoryCardPreview() {
    CategoryCard(
        category = Category("Category", 2f),
        state = LazyListState(),
        index = 1
    )
}