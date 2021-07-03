package com.example.mobiledger.data.repository

import com.example.mobiledger.data.sources.internet.InternetSource
import kotlinx.coroutines.flow.SharedFlow

interface InternetRepository {
    fun receiveInternetStatus(): SharedFlow<Boolean>
}

class InternetRepositoryImpl(private val internetSource: InternetSource) : InternetRepository {

    override fun receiveInternetStatus(): SharedFlow<Boolean> = internetSource.emitInternetStatus()
}