package com.example.mobiledger.presentation.splash

import com.example.mobiledger.common.base.BaseNavigator

interface SplashNavigator : BaseNavigator {
    fun launchDashboard()
    fun navigateToAuthScreen()
    fun navigateToOnBoarding()
}