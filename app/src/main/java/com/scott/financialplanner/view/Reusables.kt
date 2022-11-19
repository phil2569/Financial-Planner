package com.scott.financialplanner.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DefaultCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit) {
    Card(
        modifier = modifier
            .padding(start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = 6.dp,
        backgroundColor = Color.White
    ) {
        content.invoke()
    }
}