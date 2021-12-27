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
import kotlin.collections.ArrayList

class EditBudgetTemplateViewModel(
    private val budgetTemplateUseCase: BudgetTemplateUseCase,
    private val categoryUseCase: CategoryUseCase
) : BaseViewModel() {

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _budgetTemplateCategoryList: MutableLiveData<Event<List<BudgetTemplateCategoryEntity>>> = MutableLiveData()
    val budgetTemplateCategoryList: MutableLiveData<Event<List<BudgetTemplateCategoryEntity>>> = _budgetTemplateCategoryList

    private val _budgetTemplateAmount: MutableLiveData<Event<Long>> = MutableLiveData()
    val budgetTemplateAmount: MutableLiveData<Event<Long>> = _budgetTemplateAmount

    lateinit var id: String
    var budgetCategoriesList = emptyList<BudgetTemplateCategoryEntity>()
    var maxLimit: Long = 0

    var expenseCategoriesList: List<String> = listOf()
    var existingBudgetCatList: ArrayList<String> = arrayListOf()

    fun refreshData() {
        getBudgetTemplateCategoryList(id)
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
            _loadingState.value = false
        }
    }

    fun getBudgetTemplateSummary(id: String) {
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.getBudgetTemplateSummary(id)) {
                is AppResult.Success -> {
                    maxLimit = result.data.maxBudgetLimit
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
        var sum: Long = 0
        budgetCategoriesList.forEach {
            sum += it.categoryBudget
            existingBudgetCatList.add(it.category)
        }
        _budgetTemplateAmount.value = Event(sum)
    }

    //-------------------- GET BUDGET DATA --------------------
    fun getLeftOverBudgetCategoryList() {
        viewModelScope.launch {
            when (val result = categoryUseCase.getUserExpenseCategories()) {
                is AppResult.Success -> {
                    expenseCategoriesList = result.data.expenseCategoryList
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
        val finalList = ArrayList(expenseCategoriesList)
        finalList.removeAll(existingBudgetCatList)
        return finalList
    }


    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.something_went_wrong
    )
}
