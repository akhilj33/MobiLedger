package com.example.mobiledger.presentation.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.presentation.Event

class BudgetViewModel(
) : BaseViewModel() {

    val budgetViewItemListLiveData: LiveData<Event<MutableList<BudgetViewItem>>> get() = _budgetViewItemListLiveData
    private val _budgetViewItemListLiveData: MutableLiveData<Event<MutableList<BudgetViewItem>>> = MutableLiveData()


}