package com.example.mobiledger.presenation.home

import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.usecases.AuthUseCase

class HomeViewModel(
    private val authUseCase: AuthUseCase
) : BaseViewModel() {
}
