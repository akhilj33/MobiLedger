package com.example.mobiledger.presentation.recordtransaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import com.example.mobiledger.presentation.Event
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class RecordTransactionDialogFragmentViewModel(private val transactionUseCase: TransactionUseCase) : BaseViewModel() {

    private var categoryList = arrayListOf<String>()

    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<AppError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<AppError>> = _errorLiveData

    //todo : Fetch it from Firebase later
    fun provideCategoryList(): ArrayList<String> {
        categoryList.add("Rent")
        categoryList.add("Food")
        categoryList.add("Grocery")
        categoryList.add("Investment")
        categoryList.add("MISC")
        categoryList.add("Salary")
        categoryList.add("Bills")
        categoryList.add("Domestic Help")
        categoryList.add("Water")
        categoryList.add("Travel")

        return categoryList
    }

    fun addTransaction(
        monthYear: String, amount: Long, category: String, description: String,
        transactionTime: Timestamp, transactionType: String
    ) {
        viewModelScope.launch {
            getMonthlyTransactionDetail(monthYear, amount, category, description, transactionTime, transactionType)
        }
    }

    private suspend fun getMonthlyTransactionDetail(monthYear: String, amount: Long, category: String, description: String,
        transactionTime: Timestamp, transactionType: String
    ) {
        when (val result = transactionUseCase.getMonthlyTransactionSummaryFromDb(monthYear)) {
            is AppResult.Success -> {
                val monthlySummaryEntity = result.data
                val transactionEntity = TransactionEntity(amount, category, description, transactionType, transactionTime)
                addTransactionToFireBase(monthYear, transactionEntity, monthlySummaryEntity)
            }
            is AppResult.Failure -> {
                _errorLiveData.value = Event(result.error)
            }
        }
    }

    private suspend fun addTransactionToFireBase(
        monthYear: String,
        transactionEntity: TransactionEntity,
        monthlySummaryEntity: MonthlyTransactionSummaryEntity?
    ) {
        when (val result = transactionUseCase.addUserTransactionToFirebase(monthYear, transactionEntity, monthlySummaryEntity)) {
            is AppResult.Success -> {
                _dataUpdatedResult.value = Event(result.data)
            }
            is AppResult.Failure -> {
                _errorLiveData.value = Event(result.error)
            }
        }
    }
}