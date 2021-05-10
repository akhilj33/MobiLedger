package com.example.mobiledger.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.ProfileUseCase
import com.example.mobiledger.domain.usecases.TransactionUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(
    private val profileUseCase: ProfileUseCase, private val transactionUseCase: TransactionUseCase
) : BaseViewModel() {

    val userNameLiveData: LiveData<String> get() = _userNameLiveData
    private val _userNameLiveData: MutableLiveData<String> = MutableLiveData()

    fun getUserName() {
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

    fun getTransactionData(monthYear: String) {
        viewModelScope.launch {
            val monthlyData = async { transactionUseCase.getMonthlySummaryEntity(monthYear) }
            val transactionList = async { transactionUseCase.getTransactionListByMonth(monthYear) }
            when (val monthlyResult = monthlyData.await()) {
                is AppResult.Success -> {
                    val transactionResult = transactionList.await()
                    handleTransactionResult(transactionResult, monthlyResult.data)
                }
                is AppResult.Failure -> {
                    //todo
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
//                    renderHomeViewList(transactionResult.data, monthlyResult)
                }
                is AppResult.Failure -> {
                    //todo
                }
            }
        }
    }

//    private suspend fun renderHomeViewList(transactionList: List<TransactionEntity>, monthlyResult: MonthlyTransactionSummaryEntity) {
//        withContext(Dispatchers.IO) {
//            val homeViewItemList = mutableListOf<HomeViewItem>()
//            homeViewItemList.add(HomeViewItem.HeaderDataRow(R.string.overview_report))
//            homeViewItemList.add(
//                HomeViewItem.MonthlyDataRow(
//                    MonthlyData(
//                        monthlyResult.totalIncome.toString().toAmount(),
//                        monthlyResult.totalExpense.toString().toAmount()
//                    )
//                )
//            )
//
//            if (transactionList.isNotEmpty())
//                homeViewItemList.add(HomeViewItem.HeaderDataRow(R.string.latest_transaction))
//
//            transactionList.forEach {
//                homeViewItemList.add(HomeViewItem.TransactionDataRow(mapToTransactionData(it)))
//            }
//        }
//    }

//    private fun mapToTransactionData(transactionEntity: TransactionEntity): TransactionData {
//        transactionEntity.apply {
//            return TransactionData(name, amount.toString().toAmount(), transactionType, category)
//        }
//    }

    fun homeItemList() = listOf(
        HomeViewItem.HeaderDataRow(R.string.overview_report),
        HomeViewItem.MonthlyDataRow(MonthlyData("\u20B9 10000", "\u20B9 53264")),
        HomeViewItem.HeaderDataRow(R.string.latest_transaction),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),
        HomeViewItem.TransactionDataRow(getExpenseData()),
        HomeViewItem.TransactionDataRow(getIncomeData()),

        )

    private fun getIncomeData() = TransactionData("Salary", "+14000", TransactionType.Income, "Salary", R.drawable.ic_warning)
    private fun getExpenseData() = TransactionData("Electricity Bill", "-700", TransactionType.Expense, "Bills", R.drawable.ic_warning)

}
