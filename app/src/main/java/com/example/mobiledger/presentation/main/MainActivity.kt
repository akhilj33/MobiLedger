package com.example.mobiledger.presentation.main

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseActivity
import com.example.mobiledger.databinding.ActivityMainBinding
import com.example.mobiledger.presentation.NormalObserver
import com.example.mobiledger.presentation.main.MainActivityViewModel.*
import com.example.mobiledger.presentation.main.MainActivityViewModel.NavTab.*


class MainActivity :
    BaseActivity<ActivityMainBinding, MainActivityNavigator>(R.layout.activity_main) {

    private val viewModel: MainActivityViewModel by viewModels { viewModelFactory }
    private var mainActivityNavigator: MainActivityNavigator? = null
    private var currentSelectedTab: NavTab = HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityNavigator =
            MainActivityNavigator(
                getFragmentContainerID(),
                supportFragmentManager
            )

        mainActivityNavigator?.navigateToSplashScreen()
        setNavOnClickListeners()
        setupObservers()
    }

    override fun getFragmentNavigator(): MainActivityNavigator? = mainActivityNavigator

    private fun getFragmentContainerID(): Int {
        return viewBinding.fragmentContainer.id
    }


    override fun onDestroy() {
        super.onDestroy()
        mainActivityNavigator = null
    }


    private fun setNavOnClickListeners() {
        viewBinding.includeNav.homeView.setOnClickListener {
            highlightTab(NavTab.HOME)
        }
        viewBinding.includeNav.budgetView.setOnClickListener {
            highlightTab(NavTab.BUDGET())
        }
        viewBinding.includeNav.insightView.setOnClickListener {
            highlightTab(NavTab.INSIGHT())
        }
        viewBinding.includeNav.splitView.setOnClickListener {
            highlightTab(
                NavTab.SPLIT()
            )
        }
    }

    private fun resetTab() {
        ColorNav().colorBudget(R.color.colorPrimary)
        ColorNav().colorInsight(R.color.colorPrimary)
        ColorNav().colorHome(R.color.colorPrimary)
        ColorNav().colorSplit(R.color.colorPrimary)
    }

    private fun highlightTab(selectedNavTab: NavTab) {
        if (selectedNavTab != NavTab.DeselectAll) {
            currentSelectedTab = selectedNavTab
            mainActivityNavigator?.popUpToDashBoard()
        }
        viewModel.updateCurrentTab(selectedNavTab)
    }

    inner class ColorNav {

        private fun colorImage(imgView: AppCompatImageView, @ColorRes colorId: Int) {
            imgView.setColorFilter(
                ContextCompat.getColor(
                    this@MainActivity,
                    colorId
                )
            )
        }

        private fun colorText(textView: TextView, @ColorRes colorId: Int) {
            textView.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    colorId
                )
            )
        }

        fun colorHome(@ColorRes colorId: Int) {
            colorImage(viewBinding.includeNav.homeImg, colorId)
            colorText(viewBinding.includeNav.homeTitle, colorId)
        }

        fun colorBudget(@ColorRes colorId: Int) {
            colorImage(viewBinding.includeNav.budgetImg, colorId)
            colorText(viewBinding.includeNav.budgetTitle, colorId)
        }

        fun colorInsight(@ColorRes colorId: Int) {
            colorImage(viewBinding.includeNav.insightImg, colorId)
            colorText(viewBinding.includeNav.insightTitle, colorId)
        }

        fun colorSplit(@ColorRes colorId: Int) {
            colorImage(viewBinding.includeNav.splitImg, colorId)
            colorText(viewBinding.includeNav.splitTitle, colorId)

        }
    }


    /*------------------------------------------------Live Data Observers----------------------------------------------------*/

    private fun setupObservers() {

        viewModel.bottomNavVisibilityLiveData.observe(this@MainActivity, { isVisible ->
            viewBinding.includeNav.navBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        })


        viewModel.currentTab.observe(this@MainActivity, NormalObserver { tab ->
            resetTab()
            when (tab) {
                is HOME -> ColorNav().colorBudget(R.color.colorPrimary)
                is NavTab.BUDGET -> ColorNav().colorInsight(R.color.colorPrimary)
                is NavTab.INSIGHT -> ColorNav().colorSplit(R.color.colorPrimary)
                is NavTab.SPLIT -> ColorNav().colorHome(R.color.colorPrimary)
                is DeselectAll -> {
                }
            }
        })


    }
}