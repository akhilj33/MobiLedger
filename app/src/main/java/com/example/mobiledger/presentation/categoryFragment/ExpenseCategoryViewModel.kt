package com.example.mobiledger.presentation.categoryFragment

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class ExpenseCategoryViewModel(
    private val categoryUseCase: CategoryUseCase
) : BaseViewModel() {

    val expenseCategoryList: LiveData<Event<ExpenseCategoryListEntity>> get() = _expenseCategoryList
    private val _expenseCategoryList: MutableLiveData<Event<ExpenseCategoryListEntity>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    fun getExpenseCategoryList() {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.getUserExpenseCategories()) {
                is AppResult.Success -> {
                    _expenseCategoryList.value = Event(result.data)
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

    fun updateUserCategoryList(newList: List<String>) {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.updateUserExpenseCategory(ExpenseCategoryListEntity(newList))) {
                is AppResult.Success -> {
                    getExpenseCategoryList()
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

