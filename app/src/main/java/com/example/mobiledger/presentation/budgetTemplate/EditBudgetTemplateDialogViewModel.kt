package com.example.mobiledger.presentation.budgetTemplate

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.BudgetTemplateCategoryEntity
import com.example.mobiledger.domain.usecases.BudgetTemplateUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch
import java.util.*

class EditBudgetTemplateDialogViewModel(
    private val budgetTemplateUseCase: BudgetTemplateUseCase
) : BaseViewModel() {

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()
    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult

    private val _dataAdded = MutableLiveData<Boolean>(false)
    val dataAdded: LiveData<Boolean> get() = _dataAdded

    var isAddCategory: Boolean = false
    var category: String = ""
    lateinit var expenseCategoryList: ArrayList<String>
    var budgetTotal: Long = 0L
    var maxLimit: Long = 0L
    var oldBudget: Long = 0L
    var id: String = ""
    var isUpdateMaxLimit: Boolean = false

    fun addNewBudgetTemplateCategory(category: String, categoryBudget: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = budgetTemplateUseCase.addBudgetTemplateCategory(
                id, BudgetTemplateCategoryEntity(category, categoryBudget)
            )) {
                is AppResult.Success -> {
                    _isLoading.value = false
                    _dataAdded.value = true
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
            _isLoading.value = false
        }
    }

    fun updateBudgetTemplateCategoryAmount(value: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.updateBudgetCategoryAmount(id, category, value)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                    _isLoading.value = false
                }
            }
        }
    }

    fun deleteBudgetTemplateCategory() {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.deleteCategoryFromBudgetTemplate(id, category)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                    _isLoading.value = false
                }
            }
        }
    }

    fun updateBudgetTemplateMaxLimit(value: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.updateBudgetTemplateMaxLimit(id, value)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                    _isLoading.value = false
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