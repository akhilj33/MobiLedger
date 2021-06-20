package com.example.mobiledger.presentation.addtransaction

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.entities.toMutableList
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AddTransactionViewModel(
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val budgetUseCase: BudgetUseCase,
    private val userSettingsUseCase: UserSettingsUseCase
) : BaseViewModel() {

    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()

    val categoryListLiveData: LiveData<Event<MutableList<String>>> get() = _categoryListLiveData
    private val _categoryListLiveData: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _notificationIndicator = MutableLiveData<NotificationCallerData>()
    val notificationIndicator: LiveData<NotificationCallerData> get() = _notificationIndicator

    var transactionType = TransactionType.Expense
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

    fun addTransaction(monthYear: String, newTransactionEntity: TransactionEntity) {
        _loadingState.value = true
        viewModelScope.launch {
            val monthlySummaryUpdateJob =
                async {
                    transactionUseCase.updateOrAddTransactionSummary(monthYear, newTransactionEntity)
                }
            val transactionUpdateJob =
                async { transactionUseCase.updateMonthlyTransaction(monthYear, newTransactionEntity) }
            val categorySummaryAmountUpdateJob =
                async { categoryUseCase.updateOrAddMonthlyCategorySummary(monthYear, newTransactionEntity) }
            val budgetAmountUpdateJob = async {
                if (newTransactionEntity.transactionType == TransactionType.Expense)
                    budgetUseCase.updateMonthlyCategoryBudgetAmounts(
                        monthYear,
                        newTransactionEntity.category,
                        expenseChange = newTransactionEntity.amount
                    )
                else AppResult.Success(Unit)
            }

            if (transactionUpdateJob.await() is AppResult.Success && monthlySummaryUpdateJob.await() is AppResult.Success &&
                categorySummaryAmountUpdateJob.await() is AppResult.Success && budgetAmountUpdateJob.await() is AppResult.Success
            ) {
                _dataUpdatedResult.value = Event(Unit)
                if (userSettingsUseCase.isNotificationEnabled())
                _notificationIndicator.value =
                    NotificationCallerData(monthYear, newTransactionEntity.category, newTransactionEntity.amount)
            } else {
                _errorLiveData.value = Event(ViewError(viewErrorType = ViewErrorType.NON_BLOCKING))
            }
            _loadingState.value = false
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )

    data class NotificationCallerData(
        val monthYear: String,
        val expenseCategory: String,
        val expenseTransaction: Long
    )
}