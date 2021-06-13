package com.example.mobiledger.presentation.statsdetail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.extention.toPercent
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.common.utils.DateUtils.getDateInDDMMMMyyyyFormat
import com.example.mobiledger.common.utils.DateUtils.getDateInMMMMyyyyFormat
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.budget.MonthlyCategoryBudget
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.google.firebase.Timestamp
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.absoluteValue

class StatsDetailViewModel(private val categoryUseCase: CategoryUseCase, private val budgetUseCase: BudgetUseCase) : BaseViewModel() {

    val statsDetailViewItemListLiveData: LiveData<Event<List<StatsDetailViewItem>>> get() = _statsDetailViewItemListLiveData
    private val _statsDetailViewItemListLiveData: MutableLiveData<Event<List<StatsDetailViewItem>>> = MutableLiveData()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    lateinit var categoryList: List<String>
    lateinit var monthYear: Calendar
    var amount: Long = 0L

    fun getDate() = getDateInMMMMyyyyFormat(monthYear)
    fun getAbsoluteStringAmount() = amount.absoluteValue.toString()

    fun getCategoryListDetails() {
        _isLoading.value = true
        viewModelScope.launch {
            if (categoryList.isEmpty()) {
                _errorLiveData.value = Event(
                    ViewError(
                        viewErrorType = ViewErrorType.NON_BLOCKING,
                    )
                )
            } else {
                var isFailure = false
                val runningTask = categoryList.map {
                    async { getCategoryDetails(it) }
                }

                val result = runningTask.awaitAll()

                val transactionEntityList = mutableListOf<List<TransactionEntity>>()
                val monthlyCategoryBudgetList = mutableListOf<MonthlyCategoryBudget?>()

                result.forEach loop@{
                    if (it is AppResult.Success) {
                        transactionEntityList.add(it.data.first)
                        monthlyCategoryBudgetList.add(it.data.second)
                    } else if (it is AppResult.Failure) {
                        if (needToHandleAppError(it.error)) {
                            _errorLiveData.value = Event(
                                ViewError(
                                    viewErrorType = ViewErrorType.NON_BLOCKING,
                                    message = it.error.message
                                )
                            )
                        }
                        isFailure = true
                        return@loop
                    }
                }

                if (!isFailure)
                    _statsDetailViewItemListLiveData.value =
                        Event(renderViewEntity(transactionEntityList.flatten(), monthlyCategoryBudgetList))
            }
            _isLoading.value = false
        }
    }

    private suspend fun getCategoryDetails(category: String): AppResult<Pair<List<TransactionEntity>, MonthlyCategoryBudget?>> {
        return withContext(Dispatchers.IO) {
            val transactionListJob =
                async { categoryUseCase.getMonthlyCategoryTransaction(DateUtils.getDateInMMyyyyFormat(monthYear), category) }
            val categoryBudgetJob = async { budgetUseCase.getMonthlyCategoryBudget(DateUtils.getDateInMMyyyyFormat(monthYear), category) }
            when (val result = transactionListJob.await()) {
                is AppResult.Success -> {
                    val categoryResult = categoryBudgetJob.await()
                    if (categoryResult is AppResult.Success)
                        AppResult.Success(Pair(result.data, categoryResult.data))
                    else AppResult.Success(Pair(result.data, null))
                }
                is AppResult.Failure -> {
                    result
                }
            }
        }
    }

    private val headerSet = mutableSetOf<Timestamp>()

    private suspend fun renderViewEntity(
        list: List<TransactionEntity>,
        monthlyCategoryBudgetList: List<MonthlyCategoryBudget?>
    ): List<StatsDetailViewItem> {
        return withContext(Dispatchers.IO) {
            val transactionEntityList = list.sortedBy { it.transactionTime }
            val map: Map<Timestamp, Long> = list.groupBy { it.transactionTime }.mapValues { entry -> entry.value.sumOf { it.amount } }
            val lineEntryList = mutableListOf<Entry>()
            val statsDetailViewItemList = mutableListOf<StatsDetailViewItem>()
            val statsDetailGraphViewItemList = mutableListOf<StatsDetailGraphViewItem>()
            val barEntryList = mutableListOf<BarEntry>()
            val barChartLabelList = mutableListOf<Int>()
            var barEntryCount = 0f

            if (monthlyCategoryBudgetList.isNotEmpty() && list[0].transactionType == TransactionType.Expense) {
                val totalMonthlyCategoryBudget = MonthlyCategoryBudget(
                    categoryBudget = monthlyCategoryBudgetList.sumOf { it?.categoryBudget ?: 0L },
                    categoryExpense = monthlyCategoryBudgetList.sumOf { it?.categoryExpense ?: 0L }
                )

                barEntryList.add(BarEntry(barEntryCount++, totalMonthlyCategoryBudget.categoryBudget.toFloat()))
                barChartLabelList.add(R.string.budget)
                barEntryList.add(BarEntry(barEntryCount, amount.absoluteValue.toFloat()))
                barChartLabelList.add(R.string.expense)
                statsDetailGraphViewItemList.add(
                    StatsDetailGraphViewItem.BarGraphRow(barEntryList, barChartLabelList, R.string.expense_vs_budget)
                )
            }

            transactionEntityList.forEach {
                if (it.transactionTime !in headerSet) {
                    headerSet.add(it.transactionTime)
                    statsDetailViewItemList.add(
                        StatsDetailViewItem.DateRow(getDateInDDMMMMyyyyFormat(it.transactionTime), map[it.transactionTime]?.toString())
                    )
                }
                statsDetailViewItemList.add(
                    StatsDetailViewItem.TransactionDataRow(TransactionData(it, it.amount.toPercent(amount.absoluteValue).toString()))
                )
                lineEntryList.add(Entry(it.transactionTime.seconds.toFloat(), it.amount.toFloat()))
            }
            if (lineEntryList.size > 1)
                statsDetailGraphViewItemList.add(0, StatsDetailGraphViewItem.LineGraphRow(lineEntryList, R.string.monthly_analysis))
            if (statsDetailGraphViewItemList.isNotEmpty())
                statsDetailViewItemList.add(0, StatsDetailViewItem.GraphDataRow(statsDetailGraphViewItemList))
            statsDetailViewItemList
        }
    }

    fun reloadData() {
        getCategoryListDetails()
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )

}