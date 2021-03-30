package com.example.mobiledger.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.presentation.Event

class MainActivityViewModel(
) : BaseViewModel() {


    /*------------------------------------------------Live Data--------------------------------------------------*/

    private val _isInternetAvailableLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isInternetAvailableLiveData: LiveData<Event<Boolean>> get() = _isInternetAvailableLiveData


    /*---------------------------------------Internet Error Info -----------------------------------------------*/

}