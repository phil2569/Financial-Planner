package com.scott.financialplanner.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val colorPalette = lightColors(
    primary = primaryColor
)

private val typography = Typography(
    button = buttonStyle
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