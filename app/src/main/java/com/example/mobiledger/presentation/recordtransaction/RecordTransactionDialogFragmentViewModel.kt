package com.example.mobiledger.presentation.recordtransaction

import com.example.mobiledger.common.base.BaseViewModel

class RecordTransactionDialogFragmentViewModel() : BaseViewModel() {

    private var categoryList = arrayListOf<String>()

    //todo : Fetch it from Firebase later
    fun provideCategoryList(): ArrayList<String> {
        categoryList.add("Rent")
        categoryList.add("Food")
        categoryList.add("Grocery")
        categoryList.add("Investment")
        categoryList.add("MISC")
        categoryList.add("Salary")
        categoryList.add("Bills")
        categoryList.add("Domestic Help")
        categoryList.add("Water")
        categoryList.add("Travel")

        return categoryList
    }
}