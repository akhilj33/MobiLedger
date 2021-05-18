package com.example.mobiledger.common.utils

import CategoryFragment
import IncomeCategoryFragment
import androidx.fragment.app.Fragment
import com.example.mobiledger.presentation.SplitFragment
import com.example.mobiledger.presentation.auth.LoginFragment
import com.example.mobiledger.presentation.auth.SignUpFragment
import com.example.mobiledger.presentation.budget.AddBudgetDialogFragment
import com.example.mobiledger.presentation.budget.BudgetFragment
import com.example.mobiledger.presentation.categoryFragment.AddCategoryDialogFragment
import com.example.mobiledger.presentation.categoryFragment.ExpenseCategoryFragment
import com.example.mobiledger.presentation.dashboard.DashboardFragment
import com.example.mobiledger.presentation.home.HomeFragment
import com.example.mobiledger.presentation.insight.InsightFragment
import com.example.mobiledger.presentation.profile.EditProfileFragment
import com.example.mobiledger.presentation.profile.ProfileFragment
import com.example.mobiledger.presentation.recordtransaction.AddTransactionDialogFragment
import com.example.mobiledger.presentation.splash.SplashFragment

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

    fun getFragmentName(fragment: Fragment): String {
        return when (fragment) {
            is DashboardFragment -> DASHBOARD_FRAGMENT_TAG
            is SplashFragment -> SPLASH_FRAGMENT_TAG
            is LoginFragment -> LOGIN_FRAGMENT_TAG
            is SignUpFragment -> SIGN_UP_FRAGMENT_TAG
            is HomeFragment -> HOME_FRAGMENT_TAG
            is BudgetFragment -> BUDGET_FRAGMENT_TAG
            is InsightFragment -> INSIGHT_FRAGMENT_TAG
            is EditProfileFragment -> EDIT_PROFILE_FRAGMENT_TAG
            is ProfileFragment -> PROFILE_FRAGMENT_TAG
            is SplitFragment -> SPLIT_FRAGMENT_TAG
            is AddTransactionDialogFragment -> RECORD_TRANSACTION_DIALOG_FRAGMENT_TAG
            is CategoryFragment -> CATEGORY_FRAGMENT_TAG
            is IncomeCategoryFragment -> INCOME_CATEGORY_FRAGMENT_TAG
            is ExpenseCategoryFragment -> EXPENSE_CATEGORY_FRAGMENT_TAG
            is AddCategoryDialogFragment -> ADD_CATEGORY_DIALOG_FRAGMENT_TAG
            is AddBudgetDialogFragment -> ADD_BUDGET_DIALOG_FRAGMENT_TAG
            else -> throw RuntimeException("Fragment Name mapping doesn't exist in ${FragmentTagUtil.javaClass.simpleName} class")
        }
    }
}