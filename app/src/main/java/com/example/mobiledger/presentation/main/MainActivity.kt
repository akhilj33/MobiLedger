package com.example.mobiledger.presentation.main

import android.app.NotificationManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseActivity
import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.databinding.ActivityMainBinding
import com.example.mobiledger.presentation.NormalObserver
import com.example.mobiledger.presentation.main.MainActivityViewModel.*
import com.example.mobiledger.presentation.main.MainActivityViewModel.NavTab.*


class MainActivity :
    BaseActivity<ActivityMainBinding, MainActivityNavigator>(R.layout.activity_main) {

    private val viewModel: MainActivityViewModel by viewModels { viewModelFactory }
    private var mainActivityNavigator: MainActivityNavigator? = null
    private var currentSelectedTab: NavTab = HOME
    private var notificationManager: NotificationManager? = null

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

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(
            notificationManager,
            getString(R.string.channel_transaction_description),
            ConstantUtils.CHANNEL_ID_TRANSACTION
        )
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
            highlightTab(BUDGET())
        }
        viewBinding.includeNav.statsView.setOnClickListener {
            highlightTab(STATS())
        }
        viewBinding.includeNav.accountView.setOnClickListener {
            highlightTab(
                SPLIT()
            )
        }
    }

    private fun resetTab() {
        ColorNav().colorBudget(R.color.colorGrey)
        ColorNav().colorInsight(R.color.colorGrey)
        ColorNav().colorHome(R.color.colorGrey)
        ColorNav().colorSplit(R.color.colorGrey)
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
            colorImage(viewBinding.includeNav.statsImg, colorId)
            colorText(viewBinding.includeNav.statsTitle, colorId)
        }

        fun colorSplit(@ColorRes colorId: Int) {
            colorImage(viewBinding.includeNav.accountImg, colorId)
            colorText(viewBinding.includeNav.accountTitle, colorId)

        }
    }

    override fun onBackPressed() {
        if (mainActivityNavigator?.isDashBoardOnTopOfStack() == true) {
            if (viewModel.currentTab.value?.peekContent() != NavTab.HOME) highlightTab(NavTab.HOME)
            else finish()
        } else {
            if (supportFragmentManager.backStackEntryCount == 1) finish() else super.onBackPressed()
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
                is HOME -> ColorNav().colorHome(R.color.colorPrimary)
                is BUDGET -> ColorNav().colorBudget(R.color.colorPrimary)
                is STATS -> ColorNav().colorInsight(R.color.colorPrimary)
                is SPLIT -> ColorNav().colorSplit(R.color.colorPrimary)
                is DeselectAll -> {
                }
            }
        })

        viewModel.notificationIndicatorTotal.observe(this@MainActivity, {
            it.let {
                val title = "Limit Exceed!!"
                val message = "You have crossed ".plus(it.percentValue).plus("% for this month.")
                sendNotification(notificationManager, ConstantUtils.CHANNEL_ID_TRANSACTION, title, message)
            }
        })

        viewModel.notificationIndicatorCategory.observe(this@MainActivity, {
            it.let {
                val title = "Limit Exceed!!"
                val message = "You have crossed ".plus(it.percentValue)
                    .plus("% of your for category ".plus(it.notificationCallerData.expenseCategory).plus(" for this month."))
                sendNotification(notificationManager, ConstantUtils.CHANNEL_ID_TRANSACTION, title, message)
            }
        })
    }
}
