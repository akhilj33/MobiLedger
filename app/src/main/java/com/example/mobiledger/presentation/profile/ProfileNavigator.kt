package com.example.mobiledger.presentation.profile

import com.example.mobiledger.common.base.BaseNavigator

interface ProfileNavigator : BaseNavigator {
    fun navigateToEditProfileScreen()
    fun navigateToCategoryFragmentScreen()
    fun navigateToAuthScreen()
    fun navigateToBudgetTemplateFragment()
}