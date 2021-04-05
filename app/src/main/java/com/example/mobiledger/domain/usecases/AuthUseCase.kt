package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.AuthRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.AuthEntity

interface AuthUseCase {
    suspend fun loginViaEmail(email: String, password: String): AppResult<AuthEntity>
    suspend fun signUpViaEmail(email: String, password: String): AppResult<AuthEntity>
    suspend fun signInViaGoogle(idToken: String?): AppResult<AuthEntity>
    suspend fun getCurrentUser(): AppResult<AuthEntity>
    suspend fun logOut(): AppResult<Boolean>
}

class AuthUseCaseImpl(
    private val AuthRepository: AuthRepository
) : AuthUseCase {
    override suspend fun loginViaEmail(email: String, password: String): AppResult<AuthEntity> =
        AuthRepository.loginUsingEmail(email, password)

    override suspend fun signUpViaEmail(email: String, password: String): AppResult<AuthEntity>  =
        AuthRepository.signUpViaEmail(email, password)

    override suspend fun signInViaGoogle(idToken: String?): AppResult<AuthEntity> =
        AuthRepository.signInViaGoogle(idToken)

    override suspend fun getCurrentUser(): AppResult<AuthEntity> =
        AuthRepository.getCurrentUser()

    override suspend fun logOut() =
        AuthRepository.logOut()
}