package com.example.mobiledger.common.di.providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobiledger.presenation.home.HomeViewModel
import com.example.mobiledger.presenation.main.MainActivityViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactoryProvider(private val useCaseProvider: UseCaseProvider) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> {
                return MainActivityViewModel(
                ) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                return HomeViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}