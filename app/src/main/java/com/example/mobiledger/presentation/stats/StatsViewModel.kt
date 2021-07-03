package com.example.mobiledger.presentation.stats

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.extention.toAmount
import com.example.mobiledger.common.extention.toPercent
import com.example.mobiledger.common.utils.ConstantUtils.OTHERS
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.common.utils.DateUtils.getDateInMMyyyyFormat
import com.example.mobiledger.common.utils.DefaultCategoryUtils.getCategoryIcon
import com.example.mobiledger.common.utils.DefaultCategoryUtils.getOtherCategoryName
import com.example.mobiledger.common.utils.GraphUtils
import com.example.mobiledger.common.utils.GraphUtils.getGraphColorList
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.budget.MonthlyCategorySummary
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.filterList
import okhttp3.internal.toHexString
import java.util.*

class StatsViewModel(private val categoryUseCase: CategoryUseCase, private val budgetUseCase: BudgetUseCase) : BaseViewModel() {

    val statsViewItemListLiveData: LiveData<Event<MutableList<StatsViewItem>>> get() = _statsViewItemListLiveData
    private val _statsViewItemListLiveData: MutableLiveData<Event<MutableList<StatsViewItem>>> = MutableLiveData()

    val monthNameLiveData: LiveData<String> get() = _monthNameLiveData
    private val _monthNameLiveData: MutableLiveData<String> = MutableLiveData()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private var monthCount = 0

    init {
        getStatsData()
    }

    fun getStatsData(isPTR: Boolean = false) {
        _isLoading.value = true
        getAllCategoriesMonthlySummary(isPTR)
        updateMonthLiveData()
    }

    private fun getAllCategoriesMonthlySummary(isPTR: Boolean) {
        _isLoading.value = true
        viewModelScope.launch {
            val monthlyCategoryJob = async { categoryUseCase.getAllMonthlyCategories(getDateInMMyyyyFormat(getCurrentMonth()), isPTR) }
            val monthlyBudgetJob = async { budgetUseCase.getMonthlyBudgetOverView(getDateInMMyyyyFormat(getCurrentMonth())) }
            when (val result = monthlyCategoryJob.await()) {
                is AppResult.Success -> {
                    val budgetResult = monthlyBudgetJob.await()
                    _statsViewItemListLiveData.value =
                        if (budgetResult is AppResult.Success) Event(renderViewEntity(result.data, budgetResult.data))
                        else Event(renderViewEntity(result.data))
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

    private suspend fun renderViewEntity(
        monthlyCategorySummaryList: List<MonthlyCategorySummary>,
        monthlyBudget: MonthlyBudgetData? = null
    ): MutableList<StatsViewItem> {
        return withContext(Dispatchers.IO) {
            val statsGraphViewItemList = mutableListOf<StatsGraphViewItem>()
            val statsViewItemList = mutableListOf<StatsViewItem>()
            val barEntryList = mutableListOf<BarEntry>()
            val barChartLabelList = mutableListOf<Int>()
            var barEntryCount = 0f

            if (monthlyBudget != null) {
                barEntryList.add(BarEntry(barEntryCount++, monthlyBudget.maxBudget.toFloat()))
                barChartLabelList.add(R.string.monthly_limit)
                barEntryList.add(BarEntry(barEntryCount++, monthlyBudget.totalBudget.toFloat()))
                barChartLabelList.add(R.string.budget)
            }

            val incomeList =
                monthlyCategorySummaryList.filterList { categoryType == TransactionType.Income.type }
                    .sortedByDescending { it.categoryAmount }
            val expenseList =
                monthlyCategorySummaryList.filterList { categoryType == TransactionType.Expense.type }
                    .sortedByDescending { it.categoryAmount }
            val totalExpenseSum = expenseList.sumOf { it.categoryAmount }
            val totalIncomeSum = incomeList.sumOf { it.categoryAmount }

            if (expenseList.isNotEmpty()) {
                barEntryList.add(BarEntry(barEntryCount++, totalExpenseSum.toFloat()))
                barChartLabelList.add(R.string.expense)
                val expensePieAndCategoryList = getPieAndCategoryList(expenseList, TransactionType.Expense, totalExpenseSum)
                statsGraphViewItemList.add(
                    StatsGraphViewItem.PieGraphRow(
                        expensePieAndCategoryList.map { it.first },
                        R.string.expenses_breakup
                    )
                )
                statsViewItemList.add(StatsViewItem.HeaderDataRow(R.string.expense, totalExpenseSum.toAmount()))
                expensePieAndCategoryList.forEach {
                    statsViewItemList.add(StatsViewItem.CategoryDataRow(it.second))
                }
            }

            if (incomeList.isNotEmpty()) {
                barEntryList.add(BarEntry(barEntryCount++, totalIncomeSum.toFloat()))
                barChartLabelList.add(R.string.income)
                val incomePieAndCategoryList = getPieAndCategoryList(incomeList, TransactionType.Income, totalIncomeSum)
                statsGraphViewItemList.add(
                    StatsGraphViewItem.PieGraphRow(
                        incomePieAndCategoryList.map { it.first },
                        R.string.income_breakup
                    )
                )
                statsViewItemList.add(StatsViewItem.HeaderDataRow(R.string.income, totalIncomeSum.toAmount()))
                incomePieAndCategoryList.forEach {
                    statsViewItemList.add(StatsViewItem.CategoryDataRow(it.second))
                }
            }

            if (incomeList.isNotEmpty() || expenseList.isNotEmpty()) {
                barEntryList.add(BarEntry(barEntryCount, totalIncomeSum.toFloat() - totalExpenseSum.toFloat()))
                barChartLabelList.add(R.string.saving)
                statsGraphViewItemList.add(0, StatsGraphViewItem.BarGraphRow(barEntryList, barChartLabelList, R.string.income_vs_expense))
            }

            if (statsGraphViewItemList.isNotEmpty())
                statsViewItemList.add(0, StatsViewItem.GraphDataRow(statsGraphViewItemList))
            statsViewItemList
        }
    }

    private suspend fun getPieAndCategoryList(
        list: List<MonthlyCategorySummary>,
        transactionType: TransactionType,
        totalSum: Long
    ): MutableList<Pair<PieEntry, CategoryData>> {
        return withContext(Dispatchers.IO) {
            val length = list.size
            if (length < 5) {
                return@withContext getPairList(list, totalSum, transactionType)
            } else {
                val resultList = getPairList(list.subList(0, 4), totalSum, transactionType)
                val remainingSum = list.subList(4, length).sumOf { it.categoryAmount }
                val percentageValue = remainingSum.toPercent(totalSum)
                val categoryData = CategoryData(
                    OTHERS,
                    getCategoryIcon(getOtherCategoryName(transactionType), transactionType),
                    transactionType,
                    percentageValue.toString(),
                    "#" + GraphUtils.otherColor.toHexString(), remainingSum,
                    list.subList(4, length).map { it.categoryName }
                )
                resultList.add(Pair(PieEntry(percentageValue.toFloat()), categoryData))
                resultList
            }
        }
    }

    private suspend fun getPairList(
        list: List<MonthlyCategorySummary>,
        totalSum: Long,
        transactionType: TransactionType
    ): MutableList<Pair<PieEntry, CategoryData>> {
        return withContext(Dispatchers.IO) {
            val resultList = mutableListOf<Pair<PieEntry, CategoryData>>()
            list.listIterator().withIndex().forEach {
                val i = it.index
                val summary = it.value
                val percentageValue = summary.categoryAmount.toPercent(totalSum)
                val categoryData = CategoryData(
                    summary.categoryName,
                    getCategoryIcon(summary.categoryName, transactionType),
                    transactionType,
                    percentageValue.toString(),
                    "#" + getGraphColorList()[i].toHexString(), summary.categoryAmount,
                    listOf(summary.categoryName)
                )
                resultList.add(Pair(PieEntry(percentageValue.toFloat()), categoryData))
            }
            resultList
        }

    }

    private fun updateMonthLiveData() {
        _monthNameLiveData.value = DateUtils.getDateInMMMMyyyyFormat(getCurrentMonth())
    }

    fun getCurrentMonth(): Calendar = DateUtils.getCurrentDate().apply { add(Calendar.MONTH, monthCount) }

    fun isCurrentMonth(): Boolean = monthCount == 0

    fun getPreviousMonthData() {
        --monthCount
        reloadData(false)
    }

    fun getNextMonthData() {
        ++monthCount
        reloadData(false)
    }

    fun reloadData(isPTR: Boolean) {
        getStatsData(isPTR)
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}