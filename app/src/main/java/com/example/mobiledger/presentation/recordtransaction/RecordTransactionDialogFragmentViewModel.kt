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

class RecordTransactionDialogFragmentViewModel(
    private val transactionUseCase: TransactionUseCase,
    private val userSettingsUseCase: UserSettingsUseCase
) : BaseViewModel() {

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
            val uid = userSettingsUseCase.getUID()
            getMonthlyTransactionDetail(uid!!, monthYear, amount, category, description, transactionTime, transactionType)
        }
    }

    private suspend fun getMonthlyTransactionDetail(
        uid: String, monthYear: String, amount: Long, category: String, description: String,
        transactionTime: Timestamp, transactionType: String
    ) {
        when (val result = transactionUseCase.getMonthlyTransactionSummaryFromDb(uid, monthYear)) {
            is AppResult.Success -> {
                val transactionData = result.data
                val transactionId = transactionData?.noOfTransaction?.plus(1)
                val transaction = TransactionEntity(amount, category, description, transactionType, transactionTime)
                addTransactionToFireBase(uid, monthYear, transactionId.toString(), transaction, transactionData)
            }
            is AppResult.Failure -> {
                _errorLiveData.value = Event(result.error)
            }
        }
    }

    private suspend fun addTransactionToFireBase(
        uid: String,
        monthYear: String,
        transactionId: String,
        transaction: TransactionEntity,
        transactionData: MonthlyTransactionSummaryEntity?
    ) {
        when (val result = transactionUseCase.addUserTransactionToFirebase(uid, monthYear, transactionId, transaction, transactionData)) {
            is AppResult.Success -> {
                _dataUpdatedResult.value = Event(result.data)
            }
            is AppResult.Failure -> {
                _errorLiveData.value = Event(result.error)
            }
        }
    }
}