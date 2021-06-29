package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.AuthRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity

interface AuthUseCase {
    suspend fun loginViaEmail(email: String, password: String): AppResult<UserEntity>
    suspend fun signUpViaEmail(name: String, phoneNo: String, email: String, password: String): AppResult<UserEntity>
    suspend fun signInViaGoogle(idToken: String?): AppResult<Pair<Boolean, UserEntity>>
    suspend fun isUserAuthorized(): Boolean
    suspend fun logOut(): AppResult<Boolean>
    suspend fun sendPasswordResetEmail(email: String): AppResult<Unit>
}

class AuthUseCaseImpl(
    private val authRepository: AuthRepository
) : AuthUseCase {
    override suspend fun loginViaEmail(email: String, password: String): AppResult<UserEntity> =
        authRepository.loginUsingEmail(email, password)

    override suspend fun signUpViaEmail(name: String, phoneNo: String, email: String, password: String): AppResult<UserEntity> =
        authRepository.signUpViaEmail(name, phoneNo, email, password)

    override suspend fun signInViaGoogle(idToken: String?): AppResult<Pair<Boolean, UserEntity>> =
        authRepository.signInViaGoogle(idToken)

    override suspend fun isUserAuthorized(): Boolean {
        return authRepository.isUserAuthorized()
    }

    override suspend fun logOut() =
        authRepository.logOut()

    override suspend fun sendPasswordResetEmail(email: String): AppResult<Unit> = authRepository.sendPasswordResetEmail(email)
}