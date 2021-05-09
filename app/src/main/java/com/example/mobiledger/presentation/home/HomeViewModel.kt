package com.example.mobiledger.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.AuthUseCase
import com.example.mobiledger.domain.usecases.ProfileUseCase
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.Year
import java.util.*

class HomeViewModel(
    private val profileUseCase: ProfileUseCase, private val transactionUseCase: TransactionUseCase
) : BaseViewModel() {

    val userNameLiveData: LiveData<String> get() = _userNameLiveData
    private val _userNameLiveData: MutableLiveData<String> = MutableLiveData()

    fun getUserName(){
        viewModelScope.launch {
            when(val result = profileUseCase.fetchUserFromFirebase()){
                is AppResult.Success -> {
                    val name = result.data.userName?:""
                    _userNameLiveData.value = extractFirstName(name)
                }

                is AppResult.Failure -> {
                        //todo
                }
            }
        }
    }

    private fun extractFirstName(userName: String): String{
        val regex = Regex("[A-Z][a-zA-Z]*")
        return regex.find(userName.capitalize(Locale.getDefault()))?.value?:""
    }

    fun getTransactionData(monthYear: String){
        viewModelScope.launch {
            val monthlyData = async { transactionUseCase.getMonthlySummaryEntity(monthYear) }
            val transactionList = async { transactionUseCase }
        }
    }

    fun homeItemList() = listOf(
        HomeViewItem.HeaderDataRow(R.string.overview_report),
        HomeViewItem.MonthlyDataRow(MonthlyData("July 2019", "\u20B9 10000", "\u20B9 53264")),
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
