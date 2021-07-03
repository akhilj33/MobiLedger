package com.example.mobiledger.presentation.transactiondetail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.entities.toMutableList
import com.example.mobiledger.domain.enums.EditCategoryTransactionType
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.getResultFromJobs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionDetailViewModel(
    private val categoryUseCase: CategoryUseCase, private val transactionUseCase: TransactionUseCase,
    private val budgetUseCase: BudgetUseCase
) : BaseViewModel() {

    val categoryListLiveData: LiveData<Event<MutableList<String>>> get() = _categoryListLiveData
    private val _categoryListLiveData: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    val updateResultLiveData: LiveData<Event<Unit>> get() = _updateResultLiveData
    private val _updateResultLiveData: MutableLiveData<Event<Unit>> = MutableLiveData()

    lateinit var oldTransactionEntity: TransactionEntity
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

    /**
     * Cases
     * 1 - Update Monthly Summary, Transaction Entity, Category Transaction Summary
     * 2 - Update Budget Amount, if budget for the category exists [Only Expense Case]
     */
    fun updateDatabaseOnAmountChanged(monthYear: String, newTransactionEntity: TransactionEntity) {
        _loadingState.value = true
        viewModelScope.launch {
            val amountChange = newTransactionEntity.amount - oldTransactionEntity.amount
            val monthlySummaryUpdateJob =
                async {
                    transactionUseCase.updateMonthlySummerData(
                        monthYear,
                        newTransactionEntity.transactionType,
                        amountChange,
                        EditCategoryTransactionType.NOTHING
                    )
                }
            val transactionUpdateJob =
                async { transactionUseCase.updateMonthlyTransaction(monthYear, newTransactionEntity) }
            val categorySummaryAmountUpdateJob =
                async {
                    categoryUseCase.updateMonthlyCategoryAmount(
                        monthYear,
                        oldTransactionEntity,
                        amountChange,
                        EditCategoryTransactionType.NOTHING
                    )
                }
            val budgetAmountUpdateJob = async {
                if (newTransactionEntity.transactionType == TransactionType.Expense)
                    budgetUseCase.updateMonthlyCategoryBudgetAmounts(monthYear, newTransactionEntity.category, expenseChange = amountChange)
                else AppResult.Success(Unit)
            }

            when (val result = getResultFromJobs(
                listOf(
                    transactionUpdateJob, monthlySummaryUpdateJob, categorySummaryAmountUpdateJob,
                    budgetAmountUpdateJob
                )
            )) {
                is AppResult.Success -> _updateResultLiveData.value = Event(Unit)
                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
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
    }

    /**
     * Cases
     * 1-Update Monthly Summary [Only if amount is also changed], Transaction Entity
     * 2-Update old category transaction and update new category transaction [Both Amount and transaction]
     * 3-Delete Old category transaction if it had only 1 transaction
     * 4-Update category amount in old category Budget and new category Budget if they exist [Only Expense Case]
     */
    fun updateDatabaseOnCategoryChanged(monthYear: String, newTransactionEntity: TransactionEntity) {
        _loadingState.value = true
        viewModelScope.launch {
            val amountChange = newTransactionEntity.amount - oldTransactionEntity.amount
            val monthlySummaryUpdateJob =
                async {
                    if (amountChange != 0L)
                        transactionUseCase.updateMonthlySummerData(
                            monthYear,
                            newTransactionEntity.transactionType,
                            amountChange,
                            EditCategoryTransactionType.NOTHING
                        )
                    else AppResult.Success(Unit)
                }
            val transactionUpdateJob =
                async { transactionUseCase.updateMonthlyTransaction(monthYear, newTransactionEntity) }
            val categorySummaryAmountUpdateJob =
                async { categoryUseCase.updateCategoryDataOnCategoryChanged(monthYear, oldTransactionEntity, newTransactionEntity) }
            val budgetAmountUpdateJob = async {
                if (newTransactionEntity.transactionType == TransactionType.Expense)
                    budgetUseCase.updateMonthlyCategoryBudgetOnCategoryChanged(
                        monthYear,
                        oldTransactionEntity.category,
                        newTransactionEntity.category,
                        oldTransactionEntity.amount,
                        newTransactionEntity.amount
                    )
                else AppResult.Success(Unit)
            }

            when (val result = getResultFromJobs(
                listOf(
                    transactionUpdateJob, monthlySummaryUpdateJob, categorySummaryAmountUpdateJob,
                    budgetAmountUpdateJob
                )
            )) {
                is AppResult.Success -> _updateResultLiveData.value = Event(Unit)
                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
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
    }

    fun updateDatabaseOnMonthYearChanged(monthYear: String, newTransactionEntity: TransactionEntity) {
        _loadingState.value = true
        val oldMonthYear =
            DateUtils.getDateInMMyyyyFormat(DateUtils.getCalendarFromMillis(oldTransactionEntity.transactionTime.toDate().time))
        viewModelScope.launch {
            val deleteOldDataJob = async { deleteOldTransaction(oldMonthYear) }
            val addNewDataJob = async { addTransaction(monthYear, newTransactionEntity) }

            when (val result = getResultFromJobs(listOf(deleteOldDataJob, addNewDataJob))) {
                is AppResult.Success -> _updateResultLiveData.value = Event(Unit)
                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
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
    }

    /**
     * Update Transaction Entity when name, description or date changes in same month
     */
    fun updateDatabaseOnOtherFieldChanged(monthYear: String, newTransactionEntity: TransactionEntity) {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = transactionUseCase.updateMonthlyTransaction(monthYear, newTransactionEntity)) {
                is AppResult.Success -> _updateResultLiveData.value = Event(Unit)
                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
                        _errorLiveData.value = Event(
                            ViewError(
                                viewErrorType = ViewErrorType.NON_BLOCKING,
                                message = result.error.message
                            )
                        )
                    }
                }
            }
        }
    }

    fun deleteTransaction() {
        _loadingState.value = true
        val oldMonthYear =
            DateUtils.getDateInMMyyyyFormat(DateUtils.getCalendarFromMillis(oldTransactionEntity.transactionTime.toDate().time))
        viewModelScope.launch {
            when (val result = deleteOldTransaction(oldMonthYear)) {
                is AppResult.Success -> _updateResultLiveData.value = Event(Unit)
                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
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
    }

    /**
     * Cases
     * 1-Update Monthly Summary,
     * 2- Delete Transaction Entity
     * 3-Update old category summary and transaction
     * 4-Delete Old category data if it had only 1 transaction
     * 5-Update category amount in old category Budget, if they exist [Only Expense Case]
     */
    private suspend fun deleteOldTransaction(oldMonthYear: String): AppResult<Unit> {
        return withContext(Dispatchers.IO) {
            val monthlySummaryUpdateJob =
                async {
                    transactionUseCase.updateMonthlySummerData(
                        oldMonthYear,
                        oldTransactionEntity.transactionType,
                        -oldTransactionEntity.amount,
                        EditCategoryTransactionType.DELETE
                    )
                }
            val transactionDeleteJob =
                async { transactionUseCase.deleteTransaction(oldTransactionEntity.id, oldMonthYear) }
            val categorySummaryAmountUpdateJob =
                async {
                    categoryUseCase.updateMonthlyCategoryAmount(
                        oldMonthYear,
                        oldTransactionEntity,
                        -oldTransactionEntity.amount,
                        EditCategoryTransactionType.DELETE
                    )
                }
            val budgetAmountUpdateJob = async {
                if (oldTransactionEntity.transactionType == TransactionType.Expense)
                    budgetUseCase.updateMonthlyCategoryBudgetAmounts(
                        oldMonthYear,
                        oldTransactionEntity.category,
                        expenseChange = -oldTransactionEntity.amount
                    )
                else AppResult.Success(Unit)
            }

            getResultFromJobs(
                listOf(
                    transactionDeleteJob, monthlySummaryUpdateJob, categorySummaryAmountUpdateJob,
                    budgetAmountUpdateJob
                )
            )
        }
    }

    private suspend fun addTransaction(monthYear: String, newTransactionEntity: TransactionEntity): AppResult<Unit> {
        return withContext(Dispatchers.IO) {
            val monthlySummaryUpdateJob =
                async { transactionUseCase.updateOrAddTransactionSummary(monthYear, newTransactionEntity) }
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

            getResultFromJobs(
                listOf(
                    transactionUpdateJob, monthlySummaryUpdateJob, categorySummaryAmountUpdateJob,
                    budgetAmountUpdateJob
                )
            )
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}