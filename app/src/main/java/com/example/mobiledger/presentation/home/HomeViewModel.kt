package com.example.mobiledger.presentation.home

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.extention.toAmount
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.common.utils.DateUtils.getCurrentDate
import com.example.mobiledger.common.utils.DateUtils.getDateInMMMMyyyyFormat
import com.example.mobiledger.common.utils.DateUtils.getDateInMMyyyyFormat
import com.example.mobiledger.common.utils.DefaultCategoryUtils.getCategoryIcon
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.EditCategoryTransactionType
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.domain.usecases.ProfileUseCase
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.getResultFromJobs
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel(
    private val profileUseCase: ProfileUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val budgetUseCase: BudgetUseCase,
    private val categoryUseCase: CategoryUseCase
) : BaseViewModel() {

    val userNameLiveData: LiveData<String> get() = _userNameLiveData
    private val _userNameLiveData: MutableLiveData<String> = MutableLiveData()

    val homeViewItemListLiveData: LiveData<Event<MutableList<HomeViewItem>>> get() = _homeViewItemListLiveData
    private val _homeViewItemListLiveData: MutableLiveData<Event<MutableList<HomeViewItem>>> = MutableLiveData()

    val monthNameLiveData: LiveData<String> get() = _monthNameLiveData
    private val _monthNameLiveData: MutableLiveData<String> = MutableLiveData()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _deleteTransactionLiveData: MutableLiveData<Event<Int>> = MutableLiveData()
    val deleteTransactionLiveData: LiveData<Event<Int>> = _deleteTransactionLiveData

    private var monthCount = 0
    var transList: ArrayList<TransactionData> = arrayListOf()

    fun getHomeData(isPTR: Boolean) {
        _isLoading.value = true
        getUserName(isPTR)
        getTransactionData(isPTR)
        updateMonthLiveData()
    }

    private fun getUserName(isPTR: Boolean) {
        viewModelScope.launch {
            when (val result = profileUseCase.fetchUserFromFirebase(isPTR)) {
                is AppResult.Success -> {
                    val name = result.data.userName ?: ""
                    _userNameLiveData.value = extractFirstName(name)
                }

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

    private fun extractFirstName(userName: String): String {
        val regex = Regex("[A-Z][a-zA-Z]*")
        return regex.find(userName.capitalize(Locale.getDefault()))?.value ?: ""
    }

    private fun getTransactionData(isPTR: Boolean) {
        viewModelScope.launch {
            val monthlyData = async { transactionUseCase.getMonthlySummaryEntity(getDateInMMyyyyFormat(getCurrentMonth()), isPTR) }
            val transactionList = async { transactionUseCase.getTransactionListByMonth(getDateInMMyyyyFormat(getCurrentMonth()), isPTR) }
            when (val monthlyResult = monthlyData.await()) {
                is AppResult.Success -> {
                    val transactionResult = transactionList.await()
                    handleTransactionResult(transactionResult, monthlyResult.data)
                }
                is AppResult.Failure -> {
                    if (needToHandleAppError(monthlyResult.error)) {
                        _errorLiveData.value = Event(
                            ViewError(
                                viewErrorType = ViewErrorType.NON_BLOCKING,
                                message = monthlyResult.error.message
                            )
                        )
                    }
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
                    _homeViewItemListLiveData.value =
                        Event(renderHomeViewList(transactionResult.data.sortedByDescending { it.transactionTime }, monthlyResult))
                    _isLoading.value = false
                }
                is AppResult.Failure -> {
                    if (needToHandleAppError(transactionResult.error)) {
                        _errorLiveData.value = Event(
                            ViewError(
                                viewErrorType = ViewErrorType.NON_BLOCKING,
                                message = transactionResult.error.message
                            )
                        )
                    }
                    _isLoading.value = false
                }
            }
        }
    }

    private suspend fun renderHomeViewList(
        transactionList: List<TransactionEntity>,
        monthlyResult: MonthlyTransactionSummaryEntity
    ): MutableList<HomeViewItem> {
        return withContext(Dispatchers.IO) {
            val homeViewItemList = mutableListOf<HomeViewItem>()
            homeViewItemList.add(HomeViewItem.HeaderDataRow(HeaderData(R.string.overview_report)))
            homeViewItemList.add(
                HomeViewItem.MonthlyDataRow(
                    MonthlyData(
                        monthlyResult.totalIncome,
                        monthlyResult.totalExpense
                    )
                )
            )
            val pieEntryList = ArrayList<PieEntry>()
            pieEntryList.add(PieEntry(monthlyResult.totalIncome.toFloat(), "Income"))
            pieEntryList.add(PieEntry(monthlyResult.totalExpense.toFloat(), "Expense"))
            if (monthlyResult.totalIncome.toFloat() > 0 || monthlyResult.totalExpense.toFloat() > 0)
                homeViewItemList.add(HomeViewItem.MonthlyTotalPie(pieEntryList))

            if (transactionList.isNotEmpty()) {
                homeViewItemList.add(HomeViewItem.HeaderDataRow(HeaderData(R.string.latest_transaction, true)))
                var newList = transactionList
                val tempList = newList
                if (transactionList.size > 10)
                    newList = transactionList.subList(0, 10)
                newList.forEach {
                    homeViewItemList.add(HomeViewItem.TransactionDataRow(mapToTransactionData(it)))
                }
                transList.clear()
                tempList.forEach {
                    transList.add(mapToTransactionData(it))
                }

            } else {
                homeViewItemList.add(HomeViewItem.EmptyDataRow)
            }

            homeViewItemList
        }
    }

    private fun mapToTransactionData(transactionEntity: TransactionEntity): TransactionData {
        transactionEntity.apply {
            return TransactionData(
                transactionEntity = this,
                id = id,
                name = name,
                amount = amount.toAmount(),
                transactionType = transactionType,
                category = category,
                categoryIcon = getCategoryIcon(category, transactionType)
            )
        }
    }

    fun deleteTransaction(transactionEntity: TransactionEntity, position: Int) {
        _isLoading.value = true
        val oldMonthYear =
            getDateInMMyyyyFormat(DateUtils.getCalendarFromMillis(transactionEntity.transactionTime.toDate().time))
        viewModelScope.launch {
            when (val result = deleteOldTransaction(transactionEntity, oldMonthYear)) {
                is AppResult.Success -> _deleteTransactionLiveData.value = Event(position)

                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
                        _errorLiveData.value = Event(
                            ViewError(
                                viewErrorType = ViewErrorType.NON_BLOCKING,
                                message = "Cannot Delete this Item. Please Try Again"
                            )
                        )
                    }
                }
            }
            _isLoading.value = false
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
    private suspend fun deleteOldTransaction(oldTransactionEntity: TransactionEntity, oldMonthYear: String): AppResult<Unit> {
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

    private fun updateMonthLiveData() {
        _monthNameLiveData.value = getDateInMMMMyyyyFormat(getCurrentMonth())
    }

    private fun getCurrentMonth(): Calendar = getCurrentDate().apply { add(Calendar.MONTH, monthCount) }

    fun isCurrentMonth(): Boolean = monthCount == 0

    fun getPreviousMonthData() {
        --monthCount
        reloadData()
    }

    fun getNextMonthData() {
        ++monthCount
        reloadData()
    }

    fun reloadData(isPTR: Boolean = false) {
        getHomeData(isPTR)
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}
