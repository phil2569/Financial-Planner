package com.scott.financialplanner.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.scott.financialplanner.theme.FinancialPlannerTheme
import com.scott.financialplanner.viewmodel.ExpenseHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpenseHistoryActivity : AppCompatActivity() {

    private val expenseHistoryViewModel by viewModels<ExpenseHistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancialPlannerTheme {
                ExpenseHistory(expenseHistoryViewModel)
            }
        }
    }
}