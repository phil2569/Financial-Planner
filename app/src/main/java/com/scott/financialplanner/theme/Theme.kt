package com.scott.financialplanner.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


private val colorPalette = lightColors(
    primary = primaryColor,
    onBackground = primaryColor
)

private val typography = Typography(
    button = button,
    body1 = body1,
    h1 = topBar,
    h2 = header2,
    h3 = header3
)

@Composable
fun FinancialPlannerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = colorPalette,
        typography = typography,
        content = content
    )
}