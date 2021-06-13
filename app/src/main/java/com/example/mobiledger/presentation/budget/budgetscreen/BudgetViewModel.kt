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
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.budget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class BudgetViewModel(
    private val budgetUseCase: BudgetUseCase,
    private val categoryUseCase: CategoryUseCase
) : BaseViewModel() {

    val budgetViewItemListLiveData: LiveData<Event<MutableList<BudgetViewItem>>> get() = _budgetViewItemListLiveData
    private val _budgetViewItemListLiveData: MutableLiveData<Event<MutableList<BudgetViewItem>>> = MutableLiveData()

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
        getMonthlyBudgetSummary()
        updateMonthLiveData()
    }
    //-------------------- GET BUDGET DATA --------------------
    fun getExpenseCategoryList() {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.getUserExpenseCategories()) {
                is AppResult.Success -> {
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
        _isLoading.value = false
    }

    fun giveFinalExpenseList(): ArrayList<String> {
        expenseCatList.removeAll(existingBudgetCatList)
        return expenseCatList
    }

    //-------------------- GET BUDGET DATA --------------------

    private fun getMonthlyBudgetSummary() {
        _isLoading.value = true
        viewModelScope.launch {
            val monthlyBudgetCategoryList =
                async { budgetUseCase.getCategoryBudgetListByMonth(getDateInMMyyyyFormat(getCurrentMonth())) }
            val monthlyBudgetData = async { budgetUseCase.getMonthlyBudgetOverView(getDateInMMyyyyFormat(getCurrentMonth())) }
            when (val monthlyBudgetDataResult = monthlyBudgetData.await()) {
                is AppResult.Success -> {
                    val monthlyBudgetCategoryListResult = monthlyBudgetCategoryList.await()
                    handleTransactionResult(monthlyBudgetDataResult.data, monthlyBudgetCategoryListResult)
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
        monthlyBudgetCategoryList: AppResult<List<MonthlyCategoryBudget>>
    ) {
        viewModelScope.launch {
            when (monthlyBudgetCategoryList) {
                is AppResult.Success -> {
                    _budgetViewItemListLiveData.value = Event(renderBudgetViewList(monthlyBudgetCategoryList.data, monthlyBudgetData))
                    _isLoading.value = false
                }
                is AppResult.Failure -> {
                    if (needToHandleAppError(monthlyBudgetCategoryList.error)) {
                        _errorLiveData.value = Event(
                            ViewError(
                                viewErrorType = ViewErrorType.NON_BLOCKING,
                                message = monthlyBudgetCategoryList.error.message
                            )
                        )
                    }
                    _isLoading.value = false
                }
            }
        }
    }

    private suspend fun renderBudgetViewList(
        budgetCategoryList: List<MonthlyCategoryBudget>,
        monthlyResult: MonthlyBudgetData?
    ): MutableList<BudgetViewItem> {

        return withContext(Dispatchers.IO) {
            val budgetViewItemList = mutableListOf<BudgetViewItem>()
            if (monthlyResult != null) {
                budgetViewItemList.add(BudgetViewItem.BudgetHeaderData(R.string.overview_report))
                budgetViewItemList.add(
                    BudgetViewItem.BudgetOverviewData(
                        MonthlyBudgetOverviewData(
                            monthlyResult.maxBudget,
                            monthlyResult.totalBudget
                        )
                    )
                )
                budgetTotal = monthlyResult.totalBudget
                monthlyLimit = monthlyResult.maxBudget

                budgetViewItemList.add(BudgetViewItem.BtnAddCategory)

                if (budgetCategoryList.isNotEmpty()) {
                    budgetCategoryList.forEach {

                        budgetViewItemList.add(BudgetViewItem.BudgetCategory(mapToCategoryBudgetData(it)))
                        existingBudgetCatList.add(it.categoryName)
                    }
                }
            } else {
                budgetViewItemList.add(BudgetViewItem.BudgetEmpty)
            }
            budgetViewItemList
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
        @StringRes val resID: Int = R.string.generic_error_message
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
