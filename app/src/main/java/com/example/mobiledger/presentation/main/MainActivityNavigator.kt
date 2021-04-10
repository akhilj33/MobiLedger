package com.example.mobiledger.presentation.main

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.example.mobiledger.common.utils.FragmentTagUtil
import com.example.mobiledger.common.utils.FragmentTransactionHelper
import com.example.mobiledger.presentation.auth.LoginFragment
import com.example.mobiledger.presentation.auth.LoginNavigator
import com.example.mobiledger.presentation.auth.SignUpFragment
import com.example.mobiledger.presentation.auth.SignUpNavigator
import com.example.mobiledger.presentation.dashboard.DashboardFragment
import com.example.mobiledger.presentation.home.HomeNavigator
import com.example.mobiledger.presentation.profile.EditProfileFragment
import com.example.mobiledger.presentation.profile.ProfileFragment
import com.example.mobiledger.presentation.profile.ProfileNavigator
import com.example.mobiledger.presentation.splash.SplashFragment
import com.example.mobiledger.presentation.splash.SplashNavigator

class MainActivityNavigator constructor(
    @IdRes private val containerId: Int,
    private val fragmentManager: FragmentManager
) : HomeNavigator, LoginNavigator, SignUpNavigator, SplashNavigator, ProfileNavigator {


    /*---------------------------------Main Activity-------------------------------------*/

    private fun popAllFragments() {
        FragmentTransactionHelper.popAllFragments(fragmentManager)
    }

    private fun dismissAllDialogFragments() {
        FragmentTransactionHelper.dismissAllDialogs(fragmentManager)
    }

    fun popUpToDashBoard(removeDashboard: Boolean = false) {
        FragmentTransactionHelper.popFragmentFromBackStack(
            fragmentManager,
            FragmentTagUtil.DASHBOARD_FRAGMENT_TAG, isExclusive = !removeDashboard
        )
    }

    /*---------------------------------In App Navigation-------------------------------------*/

    override fun launchDashboard() {
        popAllFragments()
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            DashboardFragment.newInstance(),
            containerId, addToBackStack = false
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

    override fun navigateToProfileScreen() {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            ProfileFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    override fun navigateToEditProfileScreen() {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            EditProfileFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }
}