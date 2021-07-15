package com.example.mobiledger.presentation.categoryFragment

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class AddCategoryDialogViewModel(
    private val categoryUseCase: CategoryUseCase
) : BaseViewModel() {

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    val dataUpdatedResult: LiveData<Event<Boolean>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Boolean>> = MutableLiveData()

    fun updateUserIncomeCategoryList(newIncomeCategoryList: IncomeCategoryListEntity) {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.updateUserIncomeCategory(newIncomeCategoryList)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(true)
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

    fun updateUserExpenseCategoryList(expenseCategoryListEntity: ExpenseCategoryListEntity) {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.updateUserExpenseCategory(expenseCategoryListEntity)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(true)
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

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.something_went_wrong
    )
}