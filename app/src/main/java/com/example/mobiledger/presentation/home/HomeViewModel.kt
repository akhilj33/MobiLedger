package com.example.mobiledger.presentation.home

import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.usecases.AuthUseCase

class HomeViewModel(
    private val authUseCase: AuthUseCase
) : BaseViewModel() {
}
