package com.example.mobiledger.common.utils

import androidx.fragment.app.FragmentManager
import com.example.mobiledger.common.extention.getName
import com.example.mobiledger.presentation.recordtransaction.RecordTransactionDialogFragment

fun showRecordTransactionDialogFragment(fragmentManager: FragmentManager) {
    val dialog = RecordTransactionDialogFragment.newInstance()
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}