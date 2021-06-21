package com.example.mobiledger.presentation.budgetTemplate

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
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch
import java.util.*

class EditBudgetTemplateViewModel(
    private val budgetTemplateUseCase: BudgetTemplateUseCase,
    private val categoryUseCase: CategoryUseCase
) : BaseViewModel() {

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _budgetTemplateCategoryList: MutableLiveData<Event<List<BudgetTemplateCategoryEntity>>> = MutableLiveData()
    val budgetTemplateCategoryList: MutableLiveData<Event<List<BudgetTemplateCategoryEntity>>> = _budgetTemplateCategoryList

    private val _budgetTemplateSummary: MutableLiveData<Event<NewBudgetTemplateEntity>> = MutableLiveData()
    val budgetTemplateSummary: MutableLiveData<Event<NewBudgetTemplateEntity>> = _budgetTemplateSummary

    private val _dataDeleted = MutableLiveData<Boolean>(false)
    val dataDeleted: LiveData<Boolean> get() = _dataDeleted

    private val _totalSum = MutableLiveData<Long>(0)
    val totalSum: LiveData<Long> get() = _totalSum


    lateinit var id: String
    var toApply = false
    var budgetCategoriesList = emptyList<BudgetTemplateCategoryEntity>()
    var totalSumVal: Long = 0
    var maxLimit: Long = 0


    var expenseCatList: ArrayList<String> = arrayListOf()
    var existingBudgetCatList: ArrayList<String> = arrayListOf()

    fun refreshData() {
        getBudgetTemplateCategoryList(id)
        getBudgetTemplateSummary(id)
        getExpenseCategoryList()
    }

    fun getBudgetTemplateCategoryList(id: String) {
        _loadingState.value = true
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
        _loadingState.value = false
    }

    fun getBudgetTemplateSummary(id: String) {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.getBudgetTemplateSummary(id)) {
                is AppResult.Success -> {
                    _budgetTemplateSummary.value = Event(result.data)
                    maxLimit = Event(result.data).peekContent().maxBudgetLimit
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

    private fun getTotalAmount() {
        viewModelScope.launch {
            var sum: Long = 0
            budgetCategoriesList.forEach {
                sum += it.categoryBudget
                existingBudgetCatList.add(it.category)
            }
            _totalSum.value = sum
            totalSumVal = sum
        }
    }

    //-------------------- GET BUDGET DATA --------------------
    fun getExpenseCategoryList() {
        _loadingState.value = true
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
        _loadingState.value = false
    }

    fun deleteBudgetTemplate() {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.deleteBudgetTemplate(id)) {
                is AppResult.Success -> {
                    _dataDeleted.value = true
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                    _loadingState.value = false
                }
            }
        }
    }


    fun giveFinalExpenseList(): ArrayList<String> {
        expenseCatList.removeAll(existingBudgetCatList)
        return expenseCatList
    }


    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}
