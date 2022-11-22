package com.scott.financialplanner.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DefaultCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 6.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .padding(start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = elevation,
        backgroundColor = Color.White
    ) {
        content.invoke()
    }
}

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)? = null
) {
    Button(
        modifier = modifier.width(100.dp),
        onClick = {
            onClick?.invoke()
        }
    ) {
        Text(text = text.uppercase())
    }
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)? = null
) {
    OutlinedButton(
        modifier = modifier.width(100.dp),
        onClick = {
            onClick?.invoke()
        },
        border = ButtonDefaults.outlinedBorder.copy(width = 2.dp)
    ) {
        Text(text = text.uppercase())
    }
}

@Preview
@Composable
fun ReusablePreview() {
    SecondaryButton(text = "A button")
}