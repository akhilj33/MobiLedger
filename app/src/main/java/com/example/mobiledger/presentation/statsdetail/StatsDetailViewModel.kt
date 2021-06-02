package com.example.mobiledger.presentation.statsdetail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.extention.toPercent
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.common.utils.DateUtils.getDateInDDFormat
import com.example.mobiledger.common.utils.DateUtils.getDateInDDMMMMyyyyFormat
import com.example.mobiledger.common.utils.DateUtils.getDateInMMMMyyyyFormat
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.budget.MonthlyCategoryBudget
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.absoluteValue

class StatsDetailViewModel(private val categoryUseCase: CategoryUseCase, private val budgetUseCase: BudgetUseCase) : BaseViewModel() {

    val statsDetailViewItemListLiveData: LiveData<Event<List<StatsDetailViewItem>>> get() = _statsDetailViewItemListLiveData
    private val _statsDetailViewItemListLiveData: MutableLiveData<Event<List<StatsDetailViewItem>>> = MutableLiveData()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    lateinit var category: String
    lateinit var monthYear: Calendar
    var amount: Long = 0L

    fun getDate() = getDateInMMMMyyyyFormat(monthYear)
    fun getAbsoluteStringAmount() = amount.absoluteValue.toString()

    fun getCategoryDetails() {
        _isLoading.value = true
        viewModelScope.launch {
            val transactionListJob =
                async { categoryUseCase.getMonthlyCategoryTransaction(DateUtils.getDateInMMyyyyFormat(monthYear), category) }
            val categoryBudgetJob = async { budgetUseCase.getMonthlyCategoryBudget(DateUtils.getDateInMMyyyyFormat(monthYear), category) }
            when (val result = transactionListJob.await()) {
                is AppResult.Success -> {
                    val categoryResult = categoryBudgetJob.await()
                    _statsDetailViewItemListLiveData.value = if (categoryResult is AppResult.Success)
                        Event(renderViewEntity(result.data, categoryResult.data))
                    else
                        Event(renderViewEntity(result.data))
                    _isLoading.value = false

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
                    _isLoading.value = false
                }
            }
        }
    }

    private val headerSet = mutableSetOf<Timestamp>()

    private suspend fun renderViewEntity(
        list: List<TransactionEntity>,
        monthlyCategoryBudget: MonthlyCategoryBudget? = null
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

            if (monthlyCategoryBudget != null) {
                barEntryList.add(BarEntry(barEntryCount++, monthlyCategoryBudget.categoryBudget.toFloat()))
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
            statsDetailGraphViewItemList.add(StatsDetailGraphViewItem.LineGraphRow(lineEntryList, R.string.monthly_analysis))
            statsDetailViewItemList.add(0, StatsDetailViewItem.GraphDataRow(statsDetailGraphViewItemList))
            statsDetailViewItemList
        }
    }

    fun reloadData() {
        getCategoryDetails()
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )

}