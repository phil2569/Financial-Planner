package com.scott.financialplanner.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.constraintlayout.compose.ConstraintLayout
import com.scott.financialplanner.R
import com.scott.financialplanner.viewmodel.HomeViewModel
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.AcceptNewCategoryClicked
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.CancelNewCategoryClicked
import com.scott.financialplanner.viewmodel.HomeViewModel.HomeScreenAction.NewCategoryClicked

/**
 * The Composable located at the bottom of the home screen.
 * Handles the creation of new categories.
 */
@Composable
fun NewCategory(
    viewModel: HomeViewModel = viewModel(),
    modifier: Modifier
) {
    val homeScreenState = viewModel.homeScreenState.collectAsState().value

    Surface(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            if (homeScreenState.showNewCategoryInput) {
                NewCategoryInput(viewModel)
            }
            Button(modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.actions.trySend(NewCategoryClicked)
                }) {
                Text(text = stringResource(id = R.string.home_new_category).uppercase())
            }
        }
    }
}

@Composable
private fun NewCategoryInput(viewModel: HomeViewModel) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (cancel, accept, newCategoryTextField) = createRefs()
        val focusRequester = remember { FocusRequester() }
        var newCategoryName by remember { mutableStateOf("") }

        Image(
            modifier = Modifier
                .constrainAs(cancel) {
                    start.linkTo(parent.start)
                    end.linkTo(newCategoryTextField.start)
                    top.linkTo(newCategoryTextField.top)
                    bottom.linkTo(newCategoryTextField.bottom)
                }
                .clickable {
                    viewModel.actions.trySend(CancelNewCategoryClicked)
                },
            painter = painterResource(id = R.drawable.ic_cancel),
            contentDescription = null
        )
        OutlinedTextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .constrainAs(newCategoryTextField) {
                    start.linkTo(cancel.end)
                    end.linkTo(accept.start)
                },
            value = newCategoryName,
            onValueChange = {
                newCategoryName = it
            },
            label = { Text(stringResource(id = R.string.home_new_category)) }
        )
        Image(
            modifier = Modifier
                .constrainAs(accept) {
                    start.linkTo(newCategoryTextField.end)
                    end.linkTo(parent.end)
                    top.linkTo(newCategoryTextField.top)
                    bottom.linkTo(newCategoryTextField.bottom)
                }
                .clickable {
                    viewModel.actions.trySend(AcceptNewCategoryClicked(newCategoryName))
                },
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = null
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NewCategoryPreview() {
    NewCategory(modifier = Modifier)
}