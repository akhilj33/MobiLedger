package com.example.mobiledger.presentation.transactiondetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.entities.toMutableList
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.addtransaction.AddTransactionDialogFragmentViewModel
import kotlinx.coroutines.launch

class TransactionDetailViewModel(private val categoryUseCase: CategoryUseCase) : ViewModel() {

    val categoryListLiveData: LiveData<Event<MutableList<String>>> get() = _categoryListLiveData
    private val _categoryListLiveData: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<AddTransactionDialogFragmentViewModel.ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<AddTransactionDialogFragmentViewModel.ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    lateinit var transactionEntity: TransactionEntity
    var timeInMillis: Long? = null

    fun getIncomeCategoryList() {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.getUserIncomeCategories()) {
                is AppResult.Success -> {
                    _categoryListLiveData.value = Event(result.data.toMutableList())
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

    fun getExpenseCategoryList() {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.getUserExpenseCategories()) {
                is AppResult.Success -> {
                    _categoryListLiveData.value = Event(result.data.toMutableList())
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