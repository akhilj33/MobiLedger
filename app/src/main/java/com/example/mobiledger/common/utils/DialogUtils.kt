package com.example.mobiledger.common.utils

import androidx.fragment.app.FragmentManager
import com.example.mobiledger.common.extention.getName
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.presentation.addtransaction.AddTransactionDialogFragment
import com.example.mobiledger.presentation.budget.AddBudgetDialogFragment
import com.example.mobiledger.presentation.categoryFragment.AddCategoryDialogFragment
import com.example.mobiledger.presentation.transactiondetail.TransactionDetailDialogFragment

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
    isCategoryBudget: Boolean,
    list: ArrayList<String>,
    month: String,
    budgetTotal: Long
) {
    val dialog = AddBudgetDialogFragment.newInstance(isCategoryBudget, list, month, budgetTotal)
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}