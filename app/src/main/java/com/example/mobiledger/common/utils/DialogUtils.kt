package com.example.mobiledger.common.utils

import androidx.fragment.app.FragmentManager
import com.example.mobiledger.R
import com.example.mobiledger.common.extention.getName
import com.example.mobiledger.presentation.budget.AddBudgetDialogFragment
import com.example.mobiledger.presentation.categoryFragment.AddCategoryDialogFragment
import com.example.mobiledger.presentation.addtransaction.AddTransactionDialogFragment

fun showRecordTransactionDialogFragment(fragmentManager: FragmentManager) {
    val dialog = AddTransactionDialogFragment.newInstance()
//    dialog.isCancelable = false
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showAddCategoryDialogFragment(fragmentManager: FragmentManager, list: List<String>, isIncome: Boolean) {
    val dialog = AddCategoryDialogFragment.newInstance(list, isIncome)
//    dialog.isCancelable = false
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showAddBudgetDialogFragment(fragmentManager: FragmentManager, isCategoryBudget: Boolean) {
    val dialog = AddBudgetDialogFragment.newInstance(isCategoryBudget)
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}