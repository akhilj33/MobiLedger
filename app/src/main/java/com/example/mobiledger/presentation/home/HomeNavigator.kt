package com.example.mobiledger.presentation.home

import com.example.mobiledger.common.base.BaseNavigator

interface HomeNavigator : BaseNavigator {
    fun navigateToProfileScreen()
    fun navigateToTransactionFragmentScreen(arrayList: ArrayList<TransactionData>)
}