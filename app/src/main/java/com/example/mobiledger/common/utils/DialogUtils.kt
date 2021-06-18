package com.example.mobiledger.common.utils

import androidx.fragment.app.FragmentManager
import com.example.mobiledger.common.extention.getName
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.presentation.addtransaction.AddTransactionDialogFragment
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.budget.addbudget.AddBudgetDialogFragment
import com.example.mobiledger.presentation.budget.updatebudget.UpdateBudgetDialogFragment
import com.example.mobiledger.presentation.budgetTemplate.AddBudgetTemplateDialogFragment
import com.example.mobiledger.presentation.categoryFragment.AddCategoryDialogFragment
import com.example.mobiledger.presentation.transactiondetail.TransactionDetailDialogFragment
import java.util.*

fun showAddTransactionDialogFragment(fragmentManager: FragmentManager) {
    val dialog = AddTransactionDialogFragment.newInstance()
    dialog.isCancelable = true
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showTransactionDetailDialogFragment(transactionEntity: TransactionEntity, fragmentManager: FragmentManager) {
    val dialog = TransactionDetailDialogFragment.newInstance(transactionEntity)
    dialog.isCancelable = true
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showAddCategoryDialogFragment(fragmentManager: FragmentManager, list: List<String>, isIncome: Boolean) {
    val dialog = AddCategoryDialogFragment.newInstance(list, isIncome)
    dialog.isCancelable = true
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showAddBudgetDialogFragment(
    fragmentManager: FragmentManager,
    monthlyLimit: Long,
    list: ArrayList<String>,
    month: String,
    budgetTotal: Long,
    isAddCategory: Boolean
) {
    val dialog = AddBudgetDialogFragment.newInstance(monthlyLimit, list, month, budgetTotal, isAddCategory)
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showUpdateBudgetDialogFragment(
    fragmentManager: FragmentManager,
    month: Calendar,
    category: String,
    categoryBudget: Long,
    monthlyBudgetData: MonthlyBudgetData
) {
    val dialog = UpdateBudgetDialogFragment.newInstance(month, category, categoryBudget, monthlyBudgetData)
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showAddNewTemplateDialogFragment(
    fragmentManager: FragmentManager,
) {
    val dialog = AddBudgetTemplateDialogFragment.newInstance()
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}