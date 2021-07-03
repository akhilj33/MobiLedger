package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.InternetRepository
import kotlinx.coroutines.flow.SharedFlow

interface InternetUseCase {
    suspend fun receiveInternetStatus(): SharedFlow<Boolean>
}

class InternetUseCaseImpl(private val internetRepository: InternetRepository) : InternetUseCase {
    override suspend fun receiveInternetStatus(): SharedFlow<Boolean> = internetRepository.receiveInternetStatus()
}