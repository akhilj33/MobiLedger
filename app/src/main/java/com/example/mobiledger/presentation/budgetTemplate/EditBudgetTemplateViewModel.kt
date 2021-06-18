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
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class EditBudgetTemplateViewModel(
    private val budgetTemplateUseCase: BudgetTemplateUseCase
) : BaseViewModel() {

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _budgetTemplateCategoryList: MutableLiveData<Event<List<BudgetTemplateCategoryEntity>>> = MutableLiveData()
    val budgetTemplateCategoryList: MutableLiveData<Event<List<BudgetTemplateCategoryEntity>>> = _budgetTemplateCategoryList

    private val _budgetTemplateSummary: MutableLiveData<Event<NewBudgetTemplateEntity>> = MutableLiveData()
    val budgetTemplateSummary: MutableLiveData<Event<NewBudgetTemplateEntity>> = _budgetTemplateSummary

    private val _totalSum = MutableLiveData<Long>(0)
    val totalSum: LiveData<Long> get() = _totalSum

    private val _dataAdded = MutableLiveData<Boolean>(false)
    val dataAdded: LiveData<Boolean> get() = _dataAdded

    lateinit var id: String
    var budgetCategoriesList = emptyList<BudgetTemplateCategoryEntity>()

    fun addNewBudgetTemplateCategory(category: String, categoryBudget: Long) {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = budgetTemplateUseCase.addBudgetTemplateCategory(
                id, BudgetTemplateCategoryEntity(category, categoryBudget)
            )) {
                is AppResult.Success -> {
                    _loadingState.value = false
                    _dataAdded.value = true
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
            }
            _totalSum.value = sum
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}
