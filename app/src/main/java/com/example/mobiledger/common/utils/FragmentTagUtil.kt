package com.example.mobiledger.common.utils

import androidx.fragment.app.Fragment
import com.example.mobiledger.presentation.dashboard.DashboardFragment
import com.example.mobiledger.presentation.recordtransaction.RecordTransactionDialogFragment

object FragmentTagUtil {
    const val DASHBOARD_FRAGMENT_TAG = "DASHBOARD_FRAGMENT_TAG"
    const val RECORD_TRANSACTION_DIALOG_FRAGMENT_TAG = "RECORD_TRANSACTION_DIALOG_FRAGMENT_TAG"

    fun getFragmentName(fragment: Fragment): String {
        return when (fragment) {
            is DashboardFragment -> DASHBOARD_FRAGMENT_TAG
            is RecordTransactionDialogFragment -> RECORD_TRANSACTION_DIALOG_FRAGMENT_TAG
            else -> throw RuntimeException("Fragment Name mapping doesn't exist in ${FragmentTagUtil.javaClass.simpleName} class")
        }
    }
}