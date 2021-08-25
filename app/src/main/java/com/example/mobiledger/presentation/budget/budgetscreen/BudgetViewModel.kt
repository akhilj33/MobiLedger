package com.example.mobiledger.presentation.budget.budgetscreen

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.common.utils.DateUtils.getDateInMMyyyyFormat
import com.example.mobiledger.common.utils.DefaultCategoryUtils
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.budget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class BudgetViewModel(
    private val budgetUseCase: BudgetUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val transactionUseCase: TransactionUseCase
) : BaseViewModel() {

    val budgetViewItemListLiveData: LiveData<Event<MutableList<BudgetViewItem>>> get() = _budgetViewItemListLiveData
    private val _budgetViewItemListLiveData: MutableLiveData<Event<MutableList<BudgetViewItem>>> = MutableLiveData()

    val resetBudgetLiveData: LiveData<Event<Unit>> get() = _resetBudgetLiveData
    private val _resetBudgetLiveData: MutableLiveData<Event<Unit>> = MutableLiveData()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    val monthNameLiveData: LiveData<String> get() = _monthNameLiveData
    private val _monthNameLiveData: MutableLiveData<String> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    var expenseCatList: ArrayList<String> = arrayListOf()
    var existingBudgetCatList: ArrayList<String> = arrayListOf()

    var budgetTotal: Long = 0
    var monthlyLimit: Long = 0
    private var monthCount = 0

    fun getBudgetData() {
        _isLoading.value = true
        getMonthlyBudgetSummary()
        updateMonthLiveData()
        getExpenseCategoryList()
    }

    //-------------------- GET BUDGET DATA --------------------
    fun getExpenseCategoryList() {
        viewModelScope.launch {
            when (val result = categoryUseCase.getUserExpenseCategories()) {
                is AppResult.Success -> {
                    expenseCatList.clear()
                    expenseCatList.addAll(result.data.expenseCategoryList)
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
    }

    fun giveFinalExpenseList(): ArrayList<String> {
        expenseCatList.removeAll(existingBudgetCatList)
        return expenseCatList
    }

    //-------------------- GET BUDGET DATA --------------------

    private fun getMonthlyBudgetSummary() {
        viewModelScope.launch {
            val monthlyBudgetCategoryList =
                async { budgetUseCase.getCategoryBudgetListByMonth(getDateInMMyyyyFormat(getCurrentMonth())) }
            val monthlyBudgetData = async { budgetUseCase.getMonthlyBudgetOverView(getDateInMMyyyyFormat(getCurrentMonth())) }
            val monthlySummaryData = async { transactionUseCase.getMonthlySummaryEntity(getDateInMMyyyyFormat(getCurrentMonth())) }
            when (val monthlyBudgetDataResult = monthlyBudgetData.await()) {
                is AppResult.Success -> {
                    val monthlyBudgetCategoryListResult = monthlyBudgetCategoryList.await()
                    val monthlySummaryResult = monthlySummaryData.await()

                    val totalMonthlyExpense = if (monthlySummaryResult is AppResult.Success) monthlySummaryResult.data.totalExpense else 0
                    handleTransactionResult(monthlyBudgetDataResult.data, monthlyBudgetCategoryListResult, totalMonthlyExpense)
                }
                is AppResult.Failure -> {
                    if (needToHandleAppError(monthlyBudgetDataResult.error)) {
                        _errorLiveData.value = Event(
                            ViewError(
                                viewErrorType = ViewErrorType.NON_BLOCKING,
                                message = monthlyBudgetDataResult.error.message
                            )
                        )
                    }
                    _isLoading.value = false
                }
            }
        }
    }

    private suspend fun handleTransactionResult(
        monthlyBudgetData: MonthlyBudgetData?,
        monthlyBudgetCategoryList: AppResult<List<MonthlyCategoryBudget>>,
        totalMonthlyExpense: Long
    ) {
        viewModelScope.launch {
            when (monthlyBudgetCategoryList) {
                is AppResult.Success -> {
                    _budgetViewItemListLiveData.value = Event(renderBudgetViewList(monthlyBudgetCategoryList.data, monthlyBudgetData, totalMonthlyExpense))
                }
                is AppResult.Failure -> {
                    if (needToHandleAppError(monthlyBudgetCategoryList.error)) {
                        _errorLiveData.value = Event(
                            ViewError(viewErrorType = ViewErrorType.NON_BLOCKING, message = monthlyBudgetCategoryList.error.message)
                        )
                    }
                }
            }
            _isLoading.value = false
        }
    }

    private suspend fun renderBudgetViewList(
        budgetCategoryList: List<MonthlyCategoryBudget>,
        monthlyResult: MonthlyBudgetData?,
        totalMonthlyExpense: Long
    ): MutableList<BudgetViewItem> {

        return withContext(Dispatchers.IO) {
            val budgetViewItemList = mutableListOf<BudgetViewItem>()
            if (monthlyResult != null) {
                budgetViewItemList.add(BudgetViewItem.BudgetHeaderData(HeaderData(R.string.overview_report)))
                budgetViewItemList.add(
                    BudgetViewItem.BudgetOverviewData(
                        MonthlyBudgetOverviewData(
                            monthlyResult.maxBudget,
                            monthlyResult.totalBudget,
                            totalMonthlyExpense
                        )
                    )
                )
                budgetTotal = monthlyResult.totalBudget
                monthlyLimit = monthlyResult.maxBudget


                if (budgetCategoryList.isNotEmpty()) {
                    budgetViewItemList.add(BudgetViewItem.BudgetHeaderData(HeaderData(R.string.categories_budget, true)))
                    budgetCategoryList.forEach {

                        budgetViewItemList.add(BudgetViewItem.BudgetCategory(mapToCategoryBudgetData(it)))
                        existingBudgetCatList.add(it.categoryName)
                    }
                }
                else budgetViewItemList.add(BudgetViewItem.BudgetCategoryEmpty)
            } else {
                budgetViewItemList.add(BudgetViewItem.BudgetEmpty)
            }
            budgetViewItemList
        }
    }

    fun resetBudget(){
        _isLoading.value = true
        viewModelScope.launch {
            val currentMonth = getDateInMMyyyyFormat(getCurrentMonth())
            when(val result = budgetUseCase.deleteMonthlyBudget(currentMonth)){
                is AppResult.Success -> _resetBudgetLiveData.value = Event(Unit)
                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
                        _errorLiveData.value = Event(
                            ViewError(viewErrorType = ViewErrorType.NON_BLOCKING, message = result.error.message)
                        )
                    }
                }
            }
            _isLoading.value = false
        }
    }


    private fun updateMonthLiveData() {
        _monthNameLiveData.value = DateUtils.getDateInMMMMyyyyFormat(getCurrentMonth())
    }

    fun getCurrentMonth(): Calendar = DateUtils.getCurrentDate().apply { add(Calendar.MONTH, monthCount) }

    fun isCurrentMonth(): Boolean = monthCount == 0

    fun getPreviousMonthData() {
        --monthCount
        reloadData()
    }

    fun getNextMonthData() {
        ++monthCount
        reloadData()
    }

    fun reloadData() {
        monthlyLimit = 0
        budgetTotal = 0
        existingBudgetCatList = arrayListOf()
        getBudgetData()
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.something_went_wrong
    )
}

private fun mapToCategoryBudgetData(budgetCategoryData: MonthlyCategoryBudget): BudgetCategoryData {
    budgetCategoryData.apply {
        return BudgetCategoryData(
            categoryName = categoryName,
            totalCategoryBudget = categoryBudget,
            totalCategoryExpense = categoryExpense,
            categoryIcon = DefaultCategoryUtils.getCategoryIcon(categoryName, TransactionType.Expense)
        )
    }
}

enum class AddBudgetFragmentPurpose { ADD_CATEGORY_BUDGET, ADD_MONTHLY_LIMIT, UPDATE_MONTHLY_LIMIT }

