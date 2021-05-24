package com.example.mobiledger.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.Event

class MainActivityViewModel() : BaseViewModel() {


    /*------------------------------------------------Live Data--------------------------------------------------*/

    private val _isInternetAvailableLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isInternetAvailableLiveData: LiveData<Event<Boolean>> get() = _isInternetAvailableLiveData

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

//    fun registerInternetStatus() {
//        viewModelScope.launch {
//            internetUseCase.receiveInternetStatus().collect {
//                _isInternetAvailableLiveData.value = Event(it)
//            }
//        }
//    }

    fun addTransactionResult() {
        _addTransactionResultLiveData.value = Event(Unit)
    }

    fun addCategoryResult(transactionType: TransactionType) {
        _addCategoryResultLiveData.value = Event(transactionType)
    }

    fun addBudgetResult() {
        _addBudgetResultLiveData.value = Event(Unit)
    }

    sealed class NavTab {
        object HOME : NavTab()
        data class BUDGET(val isFromDashboard: Boolean = false) : NavTab()
        data class INSIGHT(val isFromDashboard: Boolean = false) : NavTab()
        data class SPLIT(val isFromDashboard: Boolean = false) : NavTab()
        object DeselectAll : NavTab()
    }
}