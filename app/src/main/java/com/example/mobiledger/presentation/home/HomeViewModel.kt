package com.example.mobiledger.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.extention.toAmount
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.utils.DateUtils.getCurrentDate
import com.example.mobiledger.common.utils.DateUtils.getDateInMMMMyyyyFormat
import com.example.mobiledger.common.utils.DateUtils.getDateInMMyyyyFormat
import com.example.mobiledger.common.utils.DefaultCategoryUtils.getCategoryIcon
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.usecases.ProfileUseCase
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HomeViewModel(
    private val profileUseCase: ProfileUseCase, private val transactionUseCase: TransactionUseCase
) : BaseViewModel() {

    val userNameLiveData: LiveData<String> get() = _userNameLiveData
    private val _userNameLiveData: MutableLiveData<String> = MutableLiveData()

    val homeViewItemListLiveData: LiveData<Event<MutableList<HomeViewItem>>> get() = _homeViewItemListLiveData
    private val _homeViewItemListLiveData: MutableLiveData<Event<MutableList<HomeViewItem>>> = MutableLiveData()

    val monthNameLiveData: LiveData<String> get() = _monthNameLiveData
    private val _monthNameLiveData: MutableLiveData<String> = MutableLiveData()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var monthCount = 0

    fun getHomeData(){
        _isLoading.value = true
        getUserName()
        getTransactionData()
        updateMonthLiveData()
    }

    private fun getUserName() {
        viewModelScope.launch {
            when (val result = profileUseCase.fetchUserFromFirebase()) {
                is AppResult.Success -> {
                    val name = result.data.userName ?: ""
                    _userNameLiveData.value = extractFirstName(name)
                }

                is AppResult.Failure -> {
                    //todo
                }
            }
        }
    }

    private fun extractFirstName(userName: String): String {
        val regex = Regex("[A-Z][a-zA-Z]*")
        return regex.find(userName.capitalize(Locale.getDefault()))?.value ?: ""
    }

    private fun getTransactionData() {
        viewModelScope.launch {
            val monthlyData = async { transactionUseCase.getMonthlySummaryEntity(getDateInMMyyyyFormat(getCurrentMonth())) }
            val transactionList = async { transactionUseCase.getTransactionListByMonth(getDateInMMyyyyFormat(getCurrentMonth())) }
            when (val monthlyResult = monthlyData.await()) {
                is AppResult.Success -> {
                    val transactionResult = transactionList.await()
                    handleTransactionResult(transactionResult, monthlyResult.data)
                }
                is AppResult.Failure -> {
                    //todo
                    _isLoading.value = false
                }
            }
        }
    }

    private suspend fun handleTransactionResult(
        transactionResult: AppResult<List<TransactionEntity>>, monthlyResult: MonthlyTransactionSummaryEntity
    ) {
        viewModelScope.launch {
            when (transactionResult) {
                is AppResult.Success -> {
                    _homeViewItemListLiveData.value = Event(renderHomeViewList(transactionResult.data, monthlyResult))
                    _isLoading.value = false
                }
                is AppResult.Failure -> {
                    //todo
                    _isLoading.value = false
                }
            }
        }
    }

    private suspend fun renderHomeViewList(transactionList: List<TransactionEntity>, monthlyResult: MonthlyTransactionSummaryEntity): MutableList<HomeViewItem> {
       return withContext(Dispatchers.IO) {
            val homeViewItemList = mutableListOf<HomeViewItem>()
            homeViewItemList.add(HomeViewItem.HeaderDataRow(R.string.overview_report))
            homeViewItemList.add(
                HomeViewItem.MonthlyDataRow(
                    MonthlyData(
                        monthlyResult.totalIncome.toString().toAmount(),
                        monthlyResult.totalExpense.toString().toAmount()
                    )
                )
            )

            if (transactionList.isNotEmpty()){
                homeViewItemList.add(HomeViewItem.HeaderDataRow(R.string.latest_transaction))
                var newList = transactionList
                if(transactionList.size>10)
                    newList = transactionList.subList(0,11)
                newList.forEach {
                    homeViewItemList.add(HomeViewItem.TransactionDataRow(mapToTransactionData(it)))
                }
            }

           else{
               homeViewItemList.add(HomeViewItem.EmptyDataRow)
            }

           homeViewItemList
        }
    }

    private fun mapToTransactionData(transactionEntity: TransactionEntity): TransactionData {
        transactionEntity.apply {
            return TransactionData(name, amount.toString().toAmount(), transactionType, category, getCategoryIcon(category, transactionType))
        }
    }

    fun updateMonthLiveData(){
       _monthNameLiveData.value =  getDateInMMMMyyyyFormat(getCurrentMonth())
    }

    private fun getCurrentMonth(): Calendar = getCurrentDate().apply{ add(Calendar.MONTH, monthCount)}

    fun isCurrentMonth(): Boolean = monthCount == 0

    fun getPreviousMonthData() {
        --monthCount
        reloadData()
    }

    fun getNextMonthData() {
        ++monthCount
        reloadData()
    }

    fun reloadData() {
        getHomeData()
    }

}
