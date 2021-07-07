package com.example.mobiledger.presentation.main

import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseActivity
import com.example.mobiledger.common.showAlertDialog
import com.example.mobiledger.common.showToast
import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.common.utils.DailyReminderWorker
import com.example.mobiledger.databinding.ActivityMainBinding
import com.example.mobiledger.presentation.NormalObserver
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.main.MainActivityViewModel.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity :
    BaseActivity<ActivityMainBinding, MainActivityNavigator>(R.layout.activity_main) {

    private val viewModel: MainActivityViewModel by viewModels { viewModelFactory }
    private var mainActivityNavigator: MainActivityNavigator? = null
    private var currentSelectedTab: NavTab = NavTab.HOME
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
        viewModel.registerInternetStatus()

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
            highlightTab(NavTab.BUDGET())
        }
        viewBinding.includeNav.statsView.setOnClickListener {
            highlightTab(NavTab.STATS())
        }
        viewBinding.includeNav.accountView.setOnClickListener {
            highlightTab(
                NavTab.SPLIT()
            )
        }
    }

    private fun cancelReminder() {
        WorkManager.getInstance().cancelAllWorkByTag(ConstantUtils.REMINDER_WORKER_TAG)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun switchOnDailyReminder() {

        val lastUsageTime = LocalDateTime
            .now()
            .format(DateTimeFormatter.ISO_DATE_TIME)

        val data = Data.Builder()
            .putString(
                ConstantUtils.DATA_REMINDER,
                lastUsageTime
            )
            .build()

        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
// Set Execution around 08:00:00 PM
        dueDate.set(Calendar.HOUR_OF_DAY, 20)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis.minus(currentDate.timeInMillis)

        val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag(ConstantUtils.REMINDER_WORKER_TAG).build()

        WorkManager.getInstance()
            .enqueue(dailyWorkRequest)

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

    fun shouldShowRequestPermissionRationale(permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.shouldShowRequestPermissionRationale(this, it)
    }

    fun showGoToSettingsDialog(@StringRes dialogString: Int) {
        this.let {
            val dialogTitle = getString(R.string.permission_required_dialog_title)
            val dialogMessage = getString(dialogString)
            it.showAlertDialog(
                dialogTitle,
                dialogMessage,
                getString(R.string.go_to_settings),
                getString(R.string.cancel),
                onDenyClick,
                onGoToSettingsClick
            )
        }
    }

    private val onDenyClick = {
        showToast(getString(R.string.required_permission_not_granted))
    }

    private val onGoToSettingsClick = {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(Intent.createChooser(intent, null))
    }

    /*------------------------------------------------Live Data Observers----------------------------------------------------*/

    private fun setupObservers() {

        viewModel.nativeToast.observe(this, OneTimeObserver {
            val msg = it.msg ?: if (it.msgRes != null) getString(it.msgRes) else null
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        })

        viewModel.bottomNavVisibilityLiveData.observe(this@MainActivity, { isVisible ->
            viewBinding.includeNav.navBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        })


        viewModel.currentTab.observe(this@MainActivity, NormalObserver { tab ->
            resetTab()
            when (tab) {
                is NavTab.HOME -> ColorNav().colorHome(R.color.colorPrimary)
                is NavTab.BUDGET -> ColorNav().colorBudget(R.color.colorPrimary)
                is NavTab.STATS -> ColorNav().colorInsight(R.color.colorPrimary)
                is NavTab.SPLIT -> ColorNav().colorSplit(R.color.colorPrimary)
                is NavTab.DeselectAll -> {
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

        viewModel.activateReminder.observe(this@MainActivity, {
            it.let {
                if (it.peekContent()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        switchOnDailyReminder()
                    }
                } else {
                    cancelReminder()
                }
            }
        })
    }
}
