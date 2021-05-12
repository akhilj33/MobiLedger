package com.example.mobiledger.common.utils

import androidx.fragment.app.FragmentManager
import com.example.mobiledger.common.extention.getName
import com.example.mobiledger.presentation.categoryFragment.AddCategoryDialogFragment
import com.example.mobiledger.presentation.recordtransaction.AddTransactionDialogFragment

fun showRecordTransactionDialogFragment(fragmentManager: FragmentManager) {
    val dialog = AddTransactionDialogFragment.newInstance()
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showAddCategoryDialogFragment(fragmentManager: FragmentManager, list: List<String>, isIncome: Boolean) {
    val dialog = AddCategoryDialogFragment.newInstance(list, isIncome)
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}