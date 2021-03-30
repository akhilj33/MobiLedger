package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.AuthRepository
import com.example.mobiledger.domain.AppResult
import com.google.firebase.auth.FirebaseAuth

interface AuthUseCase {
    suspend fun loginViaEmail(email: String, password: String)
}


class AuthUseCaseImpl(
    private val AuthRepository: AuthRepository
) : AuthUseCase {

    override suspend fun loginViaEmail(email: String, password: String) = AuthRepository.loginUsingEmail(email, password)
}