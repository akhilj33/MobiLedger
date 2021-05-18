package com.example.mobiledger.presentation.budget

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.extention.toAmount
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.common.utils.DefaultCategoryUtils
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class BudgetViewModel(
    private val budgetUseCase: BudgetUseCase
) : BaseViewModel() {

    val budgetViewItemListLiveData: LiveData<Event<MutableList<BudgetViewItem>>> get() = _budgetViewItemListLiveData
    private val _budgetViewItemListLiveData: MutableLiveData<Event<MutableList<BudgetViewItem>>> = MutableLiveData()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData: MutableLiveData<Event<HomeViewModel.ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<HomeViewModel.ViewError>> = _errorLiveData

    private var monthCount = 0

    private fun getCurrentMonth(): Calendar = DateUtils.getCurrentDate().apply { add(Calendar.MONTH, monthCount) }

    fun getBudgetData() {
        getMonthlyBudgetSummary()
    }

    private fun getMonthlyBudgetSummary() {
        _isLoading.value = true
        viewModelScope.launch {
            val monthlyBudgetCategoryList =
                async { budgetUseCase.getCategoryBudgetListByMonth(DateUtils.getDateInMMyyyyFormat(getCurrentMonth())) }
            val monthlyBudgetData = async { budgetUseCase.getMonthlyBudgetOverView(DateUtils.getDateInMMyyyyFormat(getCurrentMonth())) }
            when (val monthlyBudgetDataResult = monthlyBudgetData.await()) {
                is AppResult.Success -> {
                    val monthlyBudgetCategoryListResult = monthlyBudgetCategoryList.await()
                    handleTransactionResult(monthlyBudgetDataResult.data, monthlyBudgetCategoryListResult)
                }
                is AppResult.Failure -> {
                    if (needToHandleAppError(monthlyBudgetDataResult.error)) {
                        _errorLiveData.value = Event(
                            HomeViewModel.ViewError(
                                viewErrorType = HomeViewModel.ViewErrorType.NON_BLOCKING,
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
        monthlyBudgetCategoryList: AppResult<List<MonthlyCategorySummary>>
    ) {
        viewModelScope.launch {
            when (monthlyBudgetCategoryList) {
                is AppResult.Success -> {
                    _budgetViewItemListLiveData.value = Event(renderHomeViewList(monthlyBudgetCategoryList.data, monthlyBudgetData))
                    _isLoading.value = false
                }
                is AppResult.Failure -> {
                    if (needToHandleAppError(monthlyBudgetCategoryList.error)) {
                        _errorLiveData.value = Event(
                            HomeViewModel.ViewError(
                                viewErrorType = HomeViewModel.ViewErrorType.NON_BLOCKING,
                                message = monthlyBudgetCategoryList.error.message
                            )
                        )
                    }
                    _isLoading.value = false
                }
            }
        }
    }

    private suspend fun renderHomeViewList(
        budgetCategoryList: List<MonthlyCategorySummary>,
        monthlyResult: MonthlyBudgetData?
    ): MutableList<BudgetViewItem> {

        return withContext(Dispatchers.IO) {
            Log.i("Anant", "Lets make the budget list")
            val budgetViewItemList = mutableListOf<BudgetViewItem>()
            budgetViewItemList.add(BudgetViewItem.BudgetHeaderData(R.string.overview_report))
            if (monthlyResult != null) {
                Log.i("Anant", "Overview Null")
                budgetViewItemList.add(
                    BudgetViewItem.BudgetOverviewData(
                        MonthlyBudgetOverviewData(
                            monthlyResult.maxBudget.toString().toAmount(),
                            monthlyResult.totalBudget.toString().toAmount()
                        )
                    )
                )

                if (budgetCategoryList.isNotEmpty()) {
                    Log.i("Anant", budgetCategoryList.size.toString())
                    budgetCategoryList.forEach {
                        budgetViewItemList.add(BudgetViewItem.BudgetCategory(mapToCategoryBudgetData(it)))
                    }
                }
            } else {
                Log.i("Anant", "List Empty")
                budgetViewItemList.add(BudgetViewItem.BudgetEmpty)
            }


            budgetViewItemList
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )

}

private fun mapToCategoryBudgetData(budgetCategoryData: MonthlyCategorySummary): BudgetCategoryData {
    budgetCategoryData.apply {
        return BudgetCategoryData(
            categoryName = categoryName,
            totalCategoryBudget = totalCategoryBudget.toString(),
            totalCategoryExpense = totalCategoryExpense.toString(),
            categoryIcon = DefaultCategoryUtils.getCategoryIcon(categoryName, TransactionType.Expense)
        )
    }
}
