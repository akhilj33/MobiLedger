package com.example.mobiledger.presentation.budget.addbudget.applyTemplate

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.BudgetTemplateCategoryEntity
import com.example.mobiledger.domain.entities.NewBudgetTemplateEntity
import com.example.mobiledger.domain.usecases.BudgetTemplateUseCase
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.budget.MonthlyCategoryBudget
import com.example.mobiledger.presentation.budget.budgetscreen.BudgetViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ApplyTemplateViewModel(
    private val budgetUseCase: BudgetUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val budgetTemplateUseCase: BudgetTemplateUseCase
) : BaseViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _budgetTemplateCategoryList: MutableLiveData<Event<List<BudgetTemplateCategoryEntity>>> = MutableLiveData()

    private val _totalSum = MutableLiveData<Long>(0)

    private val _budgetTemplateSummary: MutableLiveData<Event<NewBudgetTemplateEntity>> = MutableLiveData()

    private val _templateApplied: MutableLiveData<Event<Unit>> = MutableLiveData()
    val templateApplied: LiveData<Event<Unit>> get() = _templateApplied

    private val _budgetTemplateList: MutableLiveData<Event<List<NewBudgetTemplateEntity>>> = MutableLiveData()
    val budgetTemplateList: MutableLiveData<Event<List<NewBudgetTemplateEntity>>> = _budgetTemplateList

    var month = ""
    var selectedId = ""
    var totalSumVal: Long = 0
    var maxLimit: Long = 0
    var budgetCategoriesList = emptyList<BudgetTemplateCategoryEntity>()
    var existingBudgetCatList: ArrayList<String> = arrayListOf()

    fun getBudgetTemplateList() {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.getBudgetTemplateList()) {
                is AppResult.Success -> {
                    _budgetTemplateList.value = Event(result.data)
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

    //-------------------- APPLY TEMPLATE --------------------

    fun applyTemplate(id: String) {
        getBudgetTemplateSummary(id)
        _isLoading.value = true
    }

    private fun getBudgetTemplateSummary(id: String) {
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.getBudgetTemplateSummary(id)) {
                is AppResult.Success -> {
                    _budgetTemplateSummary.value = Event(result.data)
                    maxLimit = Event(result.data).peekContent().maxBudgetLimit
                    getBudgetTemplateCategoryList(id)
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

    private fun getBudgetTemplateCategoryList(id: String) {
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.getBudgetTemplateCategoryList(id)) {
                is AppResult.Success -> {
                    _budgetTemplateCategoryList.value = Event(result.data)
                    budgetCategoriesList = result.data
                    getTotalAmount()
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

    private fun getTotalAmount() {
        viewModelScope.launch {
            var sum: Long = 0
            budgetCategoriesList.forEach {
                sum += it.categoryBudget
                existingBudgetCatList.add(it.category)
            }
            _totalSum.value = sum
            totalSumVal = sum
            setMonthlyBudgetLimit(MonthlyBudgetData(maxLimit, totalSumVal))
        }
    }

    private fun setMonthlyBudgetLimit(monthlyBudgetData: MonthlyBudgetData) {
        viewModelScope.launch {
            val setBudgetJob =
                async { budgetUseCase.setMonthlyBudget(month, monthlyBudgetData) }
            when (val result = setBudgetJob.await()) {
                is AppResult.Success -> {
                    addAllBudgetCategory(budgetCategoriesList)
                }

                is AppResult.Failure -> {
                    Event(
                        BudgetViewModel.ViewError(
                            viewErrorType = BudgetViewModel.ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
        }

    }

    private fun addAllBudgetCategory(budgetCategoryList: List<BudgetTemplateCategoryEntity>) {
        budgetCategoryList.forEach {
            getMonthlyCategorySummary(it.category, it.categoryBudget)
        }
        _templateApplied.value = Event(Unit)
        _isLoading.value = false
    }

    private fun getMonthlyCategorySummary(category: String, categoryBudget: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.getMonthlyCategorySummary(month, category)) {
                is AppResult.Success -> {
                    val categoryExpense = if (result.data != null) result.data.categoryAmount else 0L
                    addCategoryBudgetToFirebase(
                        category, categoryBudget, categoryExpense, month
                    )
                }
                is AppResult.Failure -> {
                    addCategoryBudgetToFirebase(category, categoryBudget, 0, month)
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

    private fun addCategoryBudgetToFirebase(category: String, categoryBudget: Long, categoryExpense: Long, month: String) {
        viewModelScope.launch {
            when (val result = budgetUseCase.addCategoryBudget(
                month, MonthlyCategoryBudget(category, categoryBudget, categoryExpense)
            )) {
                is AppResult.Success -> {

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


    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}