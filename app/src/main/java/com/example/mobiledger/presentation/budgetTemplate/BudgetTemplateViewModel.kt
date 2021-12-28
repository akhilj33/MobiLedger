package com.example.mobiledger.presentation.budgetTemplate

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.NewBudgetTemplateEntity
import com.example.mobiledger.domain.usecases.BudgetTemplateUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class BudgetTemplateViewModel(
    private val budgetTemplateUseCase: BudgetTemplateUseCase
) : BaseViewModel() {

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _budgetTemplateList: MutableLiveData<Event<List<NewBudgetTemplateEntity>>> = MutableLiveData()
    val budgetTemplateList: MutableLiveData<Event<List<NewBudgetTemplateEntity>>> = _budgetTemplateList

    private val _dataDeleted = MutableLiveData<Event<Boolean>>()
    val dataDeleted: LiveData<Event<Boolean>> get() = _dataDeleted

    val templateList: MutableList<NewBudgetTemplateEntity> = mutableListOf()

    lateinit var id: String

    fun refreshData() {
        getBudgetTemplateList()
    }

    private fun getBudgetTemplateList() {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.getBudgetTemplateList()) {
                is AppResult.Success -> {
                    templateList.clear()
                    templateList.addAll(result.data)
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
            _loadingState.value = false
        }
    }

    fun deleteBudgetTemplate() {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = budgetTemplateUseCase.deleteBudgetTemplate(id)) {
                is AppResult.Success -> {
                    _dataDeleted.value = Event(true)
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

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.something_went_wrong
    )
}