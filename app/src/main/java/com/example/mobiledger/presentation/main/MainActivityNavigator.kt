package com.example.mobiledger.presenation.main

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.example.mobiledger.common.utils.FragmentTransactionHelper
import com.example.mobiledger.presenation.home.HomeFragment
import com.example.mobiledger.presenation.home.HomeNavigator

class MainActivityNavigator constructor(
    @IdRes private val containerId: Int,
    private val fragmentManager: FragmentManager
) : HomeNavigator {


    /*---------------------------------Main Activity-------------------------------------*/

    private fun popAllFragments() {
        FragmentTransactionHelper.popAllFragments(fragmentManager)
    }

    private fun dismissAllDialogFragments() {
        FragmentTransactionHelper.dismissAllDialogs(fragmentManager)
    }

    fun navigateToHomeScreen() {
        popAllFragments()
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            HomeFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }
}