package com.example.mobiledger.presentation.main

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.example.mobiledger.common.utils.FragmentTagUtil
import com.example.mobiledger.common.utils.FragmentTransactionHelper
import com.example.mobiledger.presentation.aboutUs.AboutUsFragment
import com.example.mobiledger.presentation.auth.AuthViewPagerFragment
import com.example.mobiledger.presentation.auth.LoginNavigator
import com.example.mobiledger.presentation.auth.SignUpNavigator
import com.example.mobiledger.presentation.budget.budgetscreen.BudgetNavigator
import com.example.mobiledger.presentation.budgetTemplate.BudgetTemplateFragment
import com.example.mobiledger.presentation.budgetTemplate.BudgetTemplateNavigator
import com.example.mobiledger.presentation.budgetTemplate.EditBudgetTemplateFragment
import com.example.mobiledger.presentation.categoryFragment.CategoryFragment
import com.example.mobiledger.presentation.dashboard.DashboardFragment
import com.example.mobiledger.presentation.home.HomeNavigator
import com.example.mobiledger.presentation.home.TransactionData
import com.example.mobiledger.presentation.onBoarding.OnBoardingFragment
import com.example.mobiledger.presentation.onBoarding.OnBoardingNavigator
import com.example.mobiledger.presentation.onBoarding.TermsAndConditionFragment
import com.example.mobiledger.presentation.profile.EditProfileFragment
import com.example.mobiledger.presentation.profile.ProfileFragment
import com.example.mobiledger.presentation.profile.ProfileNavigator
import com.example.mobiledger.presentation.splash.SplashFragment
import com.example.mobiledger.presentation.splash.SplashNavigator
import com.example.mobiledger.presentation.stats.StatsNavigator
import com.example.mobiledger.presentation.statsdetail.StatsDetailFragment
import com.example.mobiledger.presentation.transactionList.TransactionListFragment
import java.util.*

class MainActivityNavigator constructor(
    @IdRes private val containerId: Int,
    private val fragmentManager: FragmentManager
) : HomeNavigator, LoginNavigator, SignUpNavigator, SplashNavigator, ProfileNavigator, BudgetNavigator, StatsNavigator,
    BudgetTemplateNavigator, OnBoardingNavigator {


    /*---------------------------------Main Activity-------------------------------------*/

    fun isDashBoardOnTopOfStack(): Boolean {
        return FragmentTransactionHelper.isFragmentOnTopOfStack(fragmentManager, FragmentTagUtil.DASHBOARD_FRAGMENT_TAG, containerId)
    }

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
            containerId, addToBackStack = true
        )
    }


    override fun navigateToAuthScreen() {
        popAllFragments()
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            AuthViewPagerFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    override fun navigateToOnBoarding() {
        popAllFragments()
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            OnBoardingFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    fun navigateToSplashScreen() {
        popAllFragments()
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            SplashFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    override fun navigateToProfileScreen() {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            ProfileFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    override fun navigateToStatsDetailScreen(categoryNameList: List<String>, amount: Long, monthYear: Calendar) {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            StatsDetailFragment.newInstance(categoryNameList, amount, monthYear),
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

    override fun navigateToCategoryFragmentScreen() {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            CategoryFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    override fun navigateToTransactionFragmentScreen(transactionList: ArrayList<TransactionData>) {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            TransactionListFragment.newInstance(transactionList),
            containerId, addToBackStack = true
        )
    }

    override fun navigateToEditBudgetTemplateScreen(id: String) {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            EditBudgetTemplateFragment.newInstance(id),
            containerId, addToBackStack = true
        )
    }

    override fun navigateToBudgetTemplateFragment() {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            BudgetTemplateFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    override fun navigateToAboutUsFragment() {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            AboutUsFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }

    override fun navigateToTermsAndCondition() {
        FragmentTransactionHelper.replaceFragment(
            fragmentManager,
            TermsAndConditionFragment.newInstance(),
            containerId, addToBackStack = true
        )
    }
}