package com.example.mobiledger.presentation.categoryFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.addtransaction.AddTransactionDialogFragmentViewModel
import kotlinx.coroutines.launch

class AddCategoryDialogViewModel(
    private val categoryUseCase: CategoryUseCase
) : BaseViewModel() {

    private val _errorLiveData: MutableLiveData<Event<AddTransactionDialogFragmentViewModel.ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<AddTransactionDialogFragmentViewModel.ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData<Boolean>(false)
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
                        AddTransactionDialogFragmentViewModel.ViewError(
                            viewErrorType = AddTransactionDialogFragmentViewModel.ViewErrorType.NON_BLOCKING,
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
                        AddTransactionDialogFragmentViewModel.ViewError(
                            viewErrorType = AddTransactionDialogFragmentViewModel.ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
        }
        _loadingState.value = false
    }
}