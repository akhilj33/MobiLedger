package com.example.mobiledger.presentation.budgetTemplate

import com.example.mobiledger.common.base.BaseNavigator

interface BudgetTemplateNavigator : BaseNavigator {
    fun navigateToEditBudgetTemplateScreen(id: String)
}