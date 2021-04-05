package com.example.mobiledger.presentation.main

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.example.mobiledger.common.utils.FragmentTransactionHelper
import com.example.mobiledger.presentation.auth.LoginFragment
import com.example.mobiledger.presentation.auth.LoginNavigator
import com.example.mobiledger.presentation.auth.SignUpFragment
import com.example.mobiledger.presentation.auth.SignUpNavigator
import com.example.mobiledger.presentation.home.HomeFragment
import com.example.mobiledger.presentation.home.HomeNavigator
import com.example.mobiledger.presentation.splash.SplashFragment
import com.example.mobiledger.presentation.splash.SplashNavigator

class MainActivityNavigator constructor(
    @IdRes private val containerId: Int,
    private val fragmentManager: FragmentManager
) : HomeNavigator, LoginNavigator, SignUpNavigator, SplashNavigator {


    /*---------------------------------Main Activity-------------------------------------*/

    private fun popAllFragments() {
        FragmentTransactionHelper.popAllFragments(fragmentManager)
    }

    private fun dismissAllDialogFragments() {
        FragmentTransactionHelper.dismissAllDialogs(fragmentManager)
    }

    /*---------------------------------In App Navigation-------------------------------------*/

    override fun navigateAuthToHomeScreen() {
        popAllFragments()
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            HomeFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    override fun navigateLoginToSignUpScreen() {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            SignUpFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    override fun navigateSplashToLoginScreen() {
        popAllFragments()
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            LoginFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    fun navigateToSplashScreen() {
        popAllFragments()
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            SplashFragment.newInstance(),
            containerId, addToBackStack = false
        )
    }
}