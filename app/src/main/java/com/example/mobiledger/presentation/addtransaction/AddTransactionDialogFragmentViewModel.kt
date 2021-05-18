package com.example.mobiledger.presentation.addtransaction

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.*
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTransactionDialogFragmentViewModel(
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase
) : BaseViewModel() {

    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()

    val categoryListLiveData: LiveData<Event<MutableList<String>>> get() = _categoryListLiveData
    private val _categoryListLiveData: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    var transactionType = TransactionType.Income
    var timeInMillis: Long? = null

    fun getIncomeCategoryList() {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.getUserIncomeCategories()) {
                is AppResult.Success -> {
                    _categoryListLiveData.value = Event(result.data.toMutableList())
                }

                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
        }
        _loadingState.value = false
    }

    fun getExpenseCategoryList() {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.getUserExpenseCategories()) {
                is AppResult.Success -> {
                    _categoryListLiveData.value = Event(result.data.toMutableList())
                }

                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
        }
        _loadingState.value = false
    }

    fun addTransaction(monthYear: String, transactionEntity: TransactionEntity) {
        viewModelScope.launch {
            _loadingState.value = true
            val addTransactionJob = async { transactionUseCase.addUserTransactionToFirebase(monthYear, transactionEntity) }
            val getMonthlySummaryJob = async { transactionUseCase.getMonthlySummaryEntity(monthYear) }
            when (val result = getMonthlySummaryJob.await()) {
                is AppResult.Success -> {
                    val transactionResult = addTransactionJob.await()
                    handleAddTransactionResult(transactionResult, result.data, transactionEntity, monthYear)
                }

                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                    _loadingState.value = false
                }
            }
        }

    }

    private suspend fun handleAddTransactionResult(
        transactionResult: AppResult<Unit>,
        monthlySummary: MonthlyTransactionSummaryEntity,
        transactionEntity: TransactionEntity,
        monthYear: String
    ) {
        viewModelScope.launch {
            when (transactionResult) {
                is AppResult.Success -> {
                    val result: AppResult<Unit> = if (monthlySummary.isEmpty()) {
                        val newMonthlySummary = getUpdatedMonthlySummary(MonthlyTransactionSummaryEntity(), transactionEntity)
                        transactionUseCase.addMonthlySummaryToFirebase(monthYear, newMonthlySummary)
                    } else {
                        val newMonthlySummary = getUpdatedMonthlySummary(monthlySummary, transactionEntity)
                        transactionUseCase.updateMonthlySummary(monthYear, newMonthlySummary)
                    }
                    handleMonthlySummaryUpdatedResult(result)
                }

                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = transactionResult.error.message
                        )
                    )
                    _loadingState.value = false
                }
            }
        }
    }

    private fun handleMonthlySummaryUpdatedResult(result: AppResult<Unit>) {
        viewModelScope.launch {
            when (result) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                    _loadingState.value = false
                }

                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                    _loadingState.value = false
                }
            }
        }
    }

    private suspend fun getUpdatedMonthlySummary(
        monthlySummary: MonthlyTransactionSummaryEntity,
        transactionEntity: TransactionEntity
    ): MonthlyTransactionSummaryEntity {
        return withContext(Dispatchers.IO) {
            val noOfTransaction = monthlySummary.noOfTransaction + 1
            var noOfIncome = monthlySummary.noOfIncomeTransaction
            var noOfExpense = monthlySummary.noOfExpenseTransaction
            var totalIncome = monthlySummary.totalIncome
            var totalExpense = monthlySummary.totalExpense
            if (transactionEntity.transactionType == TransactionType.Income) {
                noOfIncome += 1
                totalIncome = totalIncome.plus(transactionEntity.amount)
            } else if (transactionEntity.transactionType == TransactionType.Expense) {
                noOfExpense += 1
                totalExpense = totalExpense.plus(transactionEntity.amount)
            }
            val totalBalance = totalIncome - totalExpense
            MonthlyTransactionSummaryEntity(noOfTransaction, noOfIncome, noOfExpense, totalBalance, totalIncome, totalExpense)
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}