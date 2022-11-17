package com.scott.financialplanner.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scott.financialplanner.R

@Composable
fun BabyKev(modifier: Modifier) {
    Card(
        modifier = modifier.wrapContentSize(),
        elevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Image(
                painter = painterResource(id = R.drawable.kevin),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(top = 40.dp, bottom = 20.dp),
                text = stringResource(id = R.string.home_new_no_categories),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BabyKevPreview() {
    BabyKev(modifier = Modifier)
}