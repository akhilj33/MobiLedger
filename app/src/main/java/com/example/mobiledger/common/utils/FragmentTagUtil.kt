package com.example.mobiledger.common.utils

import androidx.fragment.app.Fragment
import com.example.mobiledger.presentation.SplitFragment
import com.example.mobiledger.presentation.aboutUs.AboutUsFragment
import com.example.mobiledger.presentation.addtransaction.AddTransactionDialogFragment
import com.example.mobiledger.presentation.auth.AuthViewPagerFragment
import com.example.mobiledger.presentation.auth.LoginFragment
import com.example.mobiledger.presentation.auth.SignUpFragment
import com.example.mobiledger.presentation.budget.addbudget.AddBudgetDialogFragment
import com.example.mobiledger.presentation.budget.addbudget.applyTemplate.ApplyTemplateDialogFragment
import com.example.mobiledger.presentation.budget.budgetscreen.BudgetFragment
import com.example.mobiledger.presentation.budget.updatebudget.UpdateBudgetDialogFragment
import com.example.mobiledger.presentation.budgetTemplate.AddBudgetTemplateDialogFragment
import com.example.mobiledger.presentation.budgetTemplate.BudgetTemplateFragment
import com.example.mobiledger.presentation.budgetTemplate.EditBudgetTemplateDialogFragment
import com.example.mobiledger.presentation.budgetTemplate.EditBudgetTemplateFragment
import com.example.mobiledger.presentation.categoryFragment.AddCategoryDialogFragment
import com.example.mobiledger.presentation.categoryFragment.CategoryFragment
import com.example.mobiledger.presentation.categoryFragment.ExpenseCategoryFragment
import com.example.mobiledger.presentation.categoryFragment.IncomeCategoryFragment
import com.example.mobiledger.presentation.dashboard.DashboardFragment
import com.example.mobiledger.presentation.home.HomeFragment
import com.example.mobiledger.presentation.onBoarding.OnBoardingFragment
import com.example.mobiledger.presentation.onBoarding.TermsAndConditionFragment
import com.example.mobiledger.presentation.profile.EditProfileFragment
import com.example.mobiledger.presentation.profile.ProfileFragment
import com.example.mobiledger.presentation.splash.SplashFragment
import com.example.mobiledger.presentation.stats.StatsFragment
import com.example.mobiledger.presentation.statsdetail.StatsDetailFragment
import com.example.mobiledger.presentation.transactionList.TransactionListFragment
import com.example.mobiledger.presentation.transactiondetail.TransactionDetailDialogFragment

object FragmentTagUtil {
    const val DASHBOARD_FRAGMENT_TAG = "DASHBOARD_FRAGMENT_TAG"
    private const val SPLASH_FRAGMENT_TAG = "SPLASH_FRAGMENT_TAG"
    private const val LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT_TAG"
    private const val SIGN_UP_FRAGMENT_TAG = "SIGN_UP_FRAGMENT_TAG"
    private const val HOME_FRAGMENT_TAG = "HOME_FRAGMENT_TAG"
    private const val BUDGET_FRAGMENT_TAG = "BUDGET_FRAGMENT_TAG"
    private const val INSIGHT_FRAGMENT_TAG = "INSIGHT_FRAGMENT_TAG"
    private const val EDIT_PROFILE_FRAGMENT_TAG = "EDIT_PROFILE_FRAGMENT_TAG"
    private const val PROFILE_FRAGMENT_TAG = "PROFILE_FRAGMENT_TAG"
    private const val SPLIT_FRAGMENT_TAG = "SPLIT_FRAGMENT_TAG"
    private const val RECORD_TRANSACTION_DIALOG_FRAGMENT_TAG = "RECORD_TRANSACTION_DIALOG_FRAGMENT_TAG"
    private const val CATEGORY_FRAGMENT_TAG = "CATEGORY_FRAGMENT_TAG"
    private const val INCOME_CATEGORY_FRAGMENT_TAG = "INCOME_CATEGORY_FRAGMENT_TAG"
    private const val EXPENSE_CATEGORY_FRAGMENT_TAG = "EXPENSE_CATEGORY_FRAGMENT_TAG"
    private const val ADD_CATEGORY_DIALOG_FRAGMENT_TAG = "ADD_CATEGORY_DIALOG_FRAGMENT_TAG"
    private const val ADD_BUDGET_DIALOG_FRAGMENT_TAG = "ADD_BUDGET_DIALOG_FRAGMENT_TAG"
    private const val AUTH_VIEW_PAGER_FRAGMENT = "AUTH_VIEW_PAGER_FRAGMENT_TAG"
    private const val STATS_DETAIL_FRAGMENT = "STATS_DETAIL_FRAGMENT"
    private const val TRANSACTION_LIST_FRAGMENT = "TRANSACTION_LIST_FRAGMENT"
    private const val TRANSACTION_DETAIL_FRAGMENT = "TRANSACTION_DETAIL_FRAGMENT"
    private const val UPDATE_BUDGET_FRAGMENT = "UPDATE_BUDGET_FRAGMENT"
    private const val BUDGET_TEMPLATE_FRAGMENT = "BUDGET_TEMPLATE_FRAGMENT"
    private const val EDIT_BUDGET_TEMPLATE_FRAGMENT = "EDIT_BUDGET_TEMPLATE_FRAGMENT"
    private const val ADD_BUDGET_TEMPLATE_DIALOG_FRAGMENT = "ADD_BUDGET_TEMPLATE_DIALOG_FRAGMENT"
    private const val EDIT_BUDGET_TEMPLATE_DIALOG_FRAGMENT = "EDIT_BUDGET_TEMPLATE_DIALOG_FRAGMENT"
    private const val APPLY_TEMPLATE_DIALOG_FRAGMENT = "APPLY_TEMPLATE_DIALOG_FRAGMENT"
    private const val ON_BOARDING_FRAGMENT = "ON_BOARDING_FRAGMENT"
    private const val TERMS_AND_CONDITION_FRAGMENT = "TERMS_AND_CONDITION_FRAGMENT"
    private const val ABOUT_US_FRAGMENT = "ABOUT_US_FRAGMENT"

    fun getFragmentName(fragment: Fragment): String {
        return when (fragment) {
            is DashboardFragment -> DASHBOARD_FRAGMENT_TAG
            is SplashFragment -> SPLASH_FRAGMENT_TAG
            is LoginFragment -> LOGIN_FRAGMENT_TAG
            is SignUpFragment -> SIGN_UP_FRAGMENT_TAG
            is HomeFragment -> HOME_FRAGMENT_TAG
            is BudgetFragment -> BUDGET_FRAGMENT_TAG
            is StatsFragment -> INSIGHT_FRAGMENT_TAG
            is EditProfileFragment -> EDIT_PROFILE_FRAGMENT_TAG
            is ProfileFragment -> PROFILE_FRAGMENT_TAG
            is SplitFragment -> SPLIT_FRAGMENT_TAG
            is AddTransactionDialogFragment -> RECORD_TRANSACTION_DIALOG_FRAGMENT_TAG
            is CategoryFragment -> CATEGORY_FRAGMENT_TAG
            is IncomeCategoryFragment -> INCOME_CATEGORY_FRAGMENT_TAG
            is ExpenseCategoryFragment -> EXPENSE_CATEGORY_FRAGMENT_TAG
            is AddCategoryDialogFragment -> ADD_CATEGORY_DIALOG_FRAGMENT_TAG
            is AddBudgetDialogFragment -> ADD_BUDGET_DIALOG_FRAGMENT_TAG
            is AuthViewPagerFragment -> AUTH_VIEW_PAGER_FRAGMENT
            is StatsDetailFragment -> STATS_DETAIL_FRAGMENT
            is TransactionListFragment -> TRANSACTION_LIST_FRAGMENT
            is TransactionDetailDialogFragment -> TRANSACTION_DETAIL_FRAGMENT
            is UpdateBudgetDialogFragment -> UPDATE_BUDGET_FRAGMENT
            is BudgetTemplateFragment -> BUDGET_TEMPLATE_FRAGMENT
            is EditBudgetTemplateFragment -> EDIT_BUDGET_TEMPLATE_FRAGMENT
            is AddBudgetTemplateDialogFragment -> ADD_BUDGET_TEMPLATE_DIALOG_FRAGMENT
            is EditBudgetTemplateDialogFragment -> EDIT_BUDGET_TEMPLATE_DIALOG_FRAGMENT
            is ApplyTemplateDialogFragment -> APPLY_TEMPLATE_DIALOG_FRAGMENT
            is OnBoardingFragment -> ON_BOARDING_FRAGMENT
            is TermsAndConditionFragment -> TERMS_AND_CONDITION_FRAGMENT
            is AboutUsFragment -> ABOUT_US_FRAGMENT

            else -> throw RuntimeException("Fragment Name mapping doesn't exist in ${FragmentTagUtil.javaClass.simpleName} class")
        }
    }
}