package com.example.mobiledger.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.*
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.addtransaction.AddTransactionViewModel
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.budget.MonthlyCategoryBudget
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val internetUseCase: InternetUseCase,
    private val budgetUseCase: BudgetUseCase,
    private val authUseCase: AuthUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val userSettingsUseCase: UserSettingsUseCase
) : BaseViewModel() {

    /*------------------------------------------------Live Data--------------------------------------------------*/

    private val _isInternetAvailableLiveData: MutableLiveData<Event<InternetState>> = MutableLiveData()
    val isInternetAvailableLiveData: LiveData<Event<InternetState>> get() = _isInternetAvailableLiveData

    private val _currentTab: MutableLiveData<Event<NavTab>> = MutableLiveData(Event(NavTab.HOME))
    val currentTab: LiveData<Event<NavTab>> get() = _currentTab

    private val _bottomNavVisibilityLiveData: MutableLiveData<Boolean> = MutableLiveData(true)
    val bottomNavVisibilityLiveData: LiveData<Boolean> get() = _bottomNavVisibilityLiveData

    private val _revertDeselectionInBottomNav: MutableLiveData<Event<Unit>> = MutableLiveData()
    val revertDeselectionInBottomNav: LiveData<Event<Unit>> get() = _revertDeselectionInBottomNav

    private val _addTransactionResultLiveData: MutableLiveData<Event<Unit>> = MutableLiveData()
    val addTransactionResultLiveData: LiveData<Event<Unit>> = _addTransactionResultLiveData

    private val _addCategoryResultLiveData: MutableLiveData<Event<TransactionType>> = MutableLiveData()
    val addCategoryResultLiveData: LiveData<Event<TransactionType>> = _addCategoryResultLiveData

    private val _addBudgetResultLiveData: MutableLiveData<Event<Unit>> = MutableLiveData()
    val addBudgetResultLiveData: LiveData<Event<Unit>> = _addBudgetResultLiveData

    private val _updateTransactionResultLiveData: MutableLiveData<Event<Unit>> = MutableLiveData()
    val updateTransactionResultLiveData: LiveData<Event<Unit>> = _updateTransactionResultLiveData

    private val _updateBudgetResultLiveData: MutableLiveData<Event<Unit>> = MutableLiveData()
    val updateBudgetResultLiveData: LiveData<Event<Unit>> = _updateBudgetResultLiveData

    private val _notificationIndicatorTotal = MutableLiveData<NotificationCallerPercentData>()
    val notificationIndicatorTotal: LiveData<NotificationCallerPercentData> get() = _notificationIndicatorTotal

    private val _notificationIndicatorCategory = MutableLiveData<NotificationCallerPercentData>()
    val notificationIndicatorCategory: LiveData<NotificationCallerPercentData> get() = _notificationIndicatorCategory

    private val _userLogoutLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val userLogoutLiveData: LiveData<Event<Boolean>> get() = _userLogoutLiveData

    private val _activateReminder: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val activateReminder: LiveData<Event<Boolean>> get() = _activateReminder

    private val _updateBudgetTemplateScreen: MutableLiveData<Event<Unit>> = MutableLiveData()
    val updateBudgetTemplateScreen: LiveData<Event<Unit>> = _updateBudgetTemplateScreen

    private val _addNewBudgetTemplate: MutableLiveData<Event<Unit>> = MutableLiveData()
    val addNewBudgetTemplate: LiveData<Event<Unit>> = _addNewBudgetTemplate

    private val _templateAppliedReload: MutableLiveData<Event<Unit>> = MutableLiveData()
    val templateAppliedReload: LiveData<Event<Unit>> = _templateAppliedReload
    /*---------------------------------------Bottom Tabs Info -------------------------------------------------*/

    fun updateCurrentTab(tab: NavTab) {
        _currentTab.value = Event(tab)
    }

    fun hideBottomNavigationView() {
        _bottomNavVisibilityLiveData.value = false
    }

    fun showBottomNavigationView() {
        _bottomNavVisibilityLiveData.value = true
    }

    fun revertDeselectionInBottomNav() {
        _revertDeselectionInBottomNav.value = Event(Unit)
    }

    /*---------------------------------------Internet Error Info -----------------------------------------------*/

    fun registerInternetStatus() {
        viewModelScope.launch {
            internetUseCase.receiveInternetStatus().collect { currState ->
                val events: List<Boolean> = internetUseCase.receiveInternetStatus().replayCache
                val prevState: Boolean = if (events.size > 1) events[0] else true
                _isInternetAvailableLiveData.value = Event(InternetState(prevState, currState))
            }
        }
    }

    fun addTransactionResult() {
        _addTransactionResultLiveData.value = Event(Unit)
    }

    fun addCategoryResult(transactionType: TransactionType) {
        _addCategoryResultLiveData.value = Event(transactionType)
    }

    fun addBudgetResult() {
        _addBudgetResultLiveData.value = Event(Unit)
    }

    fun activateDailyReminder(activate: Boolean) {
        _activateReminder.value = Event(activate)
    }

    fun updateTransactionResult() {
        _updateTransactionResultLiveData.value = Event(Unit)
    }

    fun updateBudgetResult() {
        _updateBudgetResultLiveData.value = Event(Unit)
    }

    fun updateUpdateBudgetFragment() {
        _updateBudgetTemplateScreen.value = Event(Unit)
    }

    fun addNewBudgetTemplate() {
        _addNewBudgetTemplate.value = Event(Unit)
    }

    fun templateApplied() {
        _templateAppliedReload.value = Event(Unit)
    }

    fun notificationHandler(notificationCallerData: AddTransactionViewModel.NotificationCallerData) {
        viewModelScope.launch {
            var maxBudget = 0L
            when (val result = budgetUseCase.getMonthlyBudgetOverView(notificationCallerData.monthYear)) {
                is AppResult.Success -> {
                    maxBudget = result.data?.maxBudget ?: 0
                    shouldTriggerTotalNotification(result.data, notificationCallerData)
                }
                is AppResult.Failure -> {
                }
            }
            when (val result =
                budgetUseCase.getMonthlyCategoryBudget(notificationCallerData.monthYear, notificationCallerData.expenseCategory)) {
                is AppResult.Success -> {
                    shouldTriggerCategoryNotification(result.data, notificationCallerData)
                }
                is AppResult.Failure -> {
                }
            }
            when (val result =
                transactionUseCase.getMonthlySummaryEntity(notificationCallerData.monthYear, false)) {
                is AppResult.Success -> {
                    shouldTriggerTotalExpenseNotification(result.data, notificationCallerData, maxBudget)
                }
                is AppResult.Failure -> {
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            when (authUseCase.logOut()) {
                is AppResult.Success -> {
                    userSettingsUseCase.clearDataOnLogout()
                    _userLogoutLiveData.value = Event(true)
                }
                is AppResult.Failure -> {
                    _userLogoutLiveData.value = Event(false)
                }
            }

        }
    }

    private fun shouldTriggerTotalNotification(
        monthlyBudgetData: MonthlyBudgetData?,
        notificationCallerData: AddTransactionViewModel.NotificationCallerData
    ) {
        if (monthlyBudgetData?.maxBudget != null && (monthlyBudgetData.totalBudget.toFloat() / monthlyBudgetData.maxBudget.toFloat()) >= 1 && ((monthlyBudgetData.totalBudget.toFloat() - notificationCallerData.expenseTransaction.toFloat()) / monthlyBudgetData.maxBudget.toFloat()) < 1) {
            _notificationIndicatorTotal.value = NotificationCallerPercentData(notificationCallerData, 100)
        } else if (monthlyBudgetData?.maxBudget != null && (monthlyBudgetData.totalBudget.toFloat() / monthlyBudgetData.maxBudget.toFloat()) >= .9 && ((monthlyBudgetData.totalBudget.toFloat() - notificationCallerData.expenseTransaction.toFloat()) / monthlyBudgetData.maxBudget.toFloat()) < .9) {
            _notificationIndicatorTotal.value = NotificationCallerPercentData(notificationCallerData, 90)
        } else if (monthlyBudgetData?.maxBudget != null && (monthlyBudgetData.totalBudget.toFloat() / monthlyBudgetData.maxBudget.toFloat()) >= .5 && ((monthlyBudgetData.totalBudget.toFloat() - notificationCallerData.expenseTransaction.toFloat()) / monthlyBudgetData.maxBudget.toFloat()) < .5) {
            _notificationIndicatorTotal.value = NotificationCallerPercentData(notificationCallerData, 50)
        }
    }

    private fun shouldTriggerCategoryNotification(
        monthlyCategoryBudget: MonthlyCategoryBudget?,
        notificationCallerData: AddTransactionViewModel.NotificationCallerData
    ) {
        if (monthlyCategoryBudget?.categoryBudget != null && (monthlyCategoryBudget.categoryExpense.toFloat() / monthlyCategoryBudget.categoryBudget.toFloat()) >= 1 && ((monthlyCategoryBudget.categoryExpense.toFloat() - notificationCallerData.expenseTransaction.toFloat()) / monthlyCategoryBudget.categoryBudget.toFloat()) < 1) {
            _notificationIndicatorCategory.value = NotificationCallerPercentData(notificationCallerData, 100)
        } else if (monthlyCategoryBudget?.categoryBudget != null && (monthlyCategoryBudget.categoryExpense.toFloat() / monthlyCategoryBudget.categoryBudget.toFloat()) >= .9 && ((monthlyCategoryBudget.categoryExpense.toFloat() - notificationCallerData.expenseTransaction.toFloat()) / monthlyCategoryBudget.categoryBudget.toFloat()) < .9) {
            _notificationIndicatorCategory.value = NotificationCallerPercentData(notificationCallerData, 90)
        } else if (monthlyCategoryBudget?.categoryBudget != null && (monthlyCategoryBudget.categoryExpense.toFloat() / monthlyCategoryBudget.categoryBudget.toFloat()) >= .5 && ((monthlyCategoryBudget.categoryExpense.toFloat() - notificationCallerData.expenseTransaction.toFloat()) / monthlyCategoryBudget.categoryBudget.toFloat()) < .5) {
            _notificationIndicatorCategory.value = NotificationCallerPercentData(notificationCallerData, 50)
        }
    }

    private fun shouldTriggerTotalExpenseNotification(
        monthlySummaryData: MonthlyTransactionSummaryEntity,
        notificationCallerData: AddTransactionViewModel.NotificationCallerData,
        maxBudget: Long
    ) {
        if (monthlySummaryData.totalExpense.toFloat() / maxBudget.toFloat() >= 1 && ((monthlySummaryData.totalExpense.toFloat() - notificationCallerData.expenseTransaction.toFloat()) / maxBudget.toFloat()) < 1) {
            _notificationIndicatorTotal.value = NotificationCallerPercentData(notificationCallerData, 100)
        } else if (monthlySummaryData.totalExpense.toFloat() / maxBudget.toFloat() >= .9 && ((monthlySummaryData.totalExpense.toFloat() - notificationCallerData.expenseTransaction.toFloat()) / maxBudget.toFloat()) < .9) {
            _notificationIndicatorTotal.value = NotificationCallerPercentData(notificationCallerData, 90)
        } else if (monthlySummaryData.totalExpense.toFloat() / maxBudget.toFloat() >= .5 && ((monthlySummaryData.totalExpense.toFloat() - notificationCallerData.expenseTransaction.toFloat()) / maxBudget.toFloat()) < .5) {
            _notificationIndicatorTotal.value = NotificationCallerPercentData(notificationCallerData, 50)
        }
    }

    /*----------------------------Android Permissions Check---------------------------------*/

    suspend fun setPermissionNotFirstTime(permissions: Array<String>) {
        userSettingsUseCase.setIsFirstTimePermissionAsked(permissions)
    }

    suspend fun isPermissionsFirstTime(permissions: Array<String>): Boolean {
        return userSettingsUseCase.isFirstTimePermissionAsked(permissions)
    }
}

data class InternetState(val previous : Boolean, val current: Boolean)

sealed class NavTab {
    object HOME : NavTab()
    data class BUDGET(val isFromDashboard: Boolean = false) : NavTab()
    data class STATS(val isFromDashboard: Boolean = false) : NavTab()
    data class PROFILE(val isFromDashboard: Boolean = false) : NavTab()
    object DeselectAll : NavTab()
}

data class NotificationCallerPercentData(
    val notificationCallerData: AddTransactionViewModel.NotificationCallerData,
    val percentValue: Long
)