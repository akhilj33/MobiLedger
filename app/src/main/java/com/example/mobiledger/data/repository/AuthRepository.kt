package com.example.mobiledger.data.repository

import com.example.mobiledger.data.sources.api.model.AuthSource
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.AuthEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface AuthRepository {
    suspend fun loginUsingEmail(email: String, password: String): AppResult<AuthEntity>
    suspend fun signUpViaEmail(email: String, password: String): AppResult<AuthEntity>
    suspend fun signInViaGoogle(idToken: String?): AppResult<AuthEntity>
    suspend fun getCurrentUser(): AppResult<AuthEntity>
    suspend fun logOut(): AppResult<Boolean>
}

class AuthRepositoryImpl(
    private val authSource: AuthSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {
    override suspend fun loginUsingEmail(email: String, password: String): AppResult<AuthEntity> {
        return withContext(dispatcher) {
            authSource.loginUserViaEmail(email, password)
        }
    }

    override suspend fun signUpViaEmail(email: String, password: String): AppResult<AuthEntity> {
        return withContext(dispatcher) {
            authSource.signUpViaEmail(email, password)
        }
    }

    override suspend fun signInViaGoogle(idToken: String?): AppResult<AuthEntity> {
        return withContext(dispatcher) {
            authSource.signInViaGoogle(idToken)
        }
    }

    override suspend fun getCurrentUser(): AppResult<AuthEntity> {
        return withContext(dispatcher) {
            authSource.getCurrentUser()
        }
    }

    override suspend fun logOut(): AppResult<Boolean> {
        return withContext(dispatcher){
            authSource.logOut()
        }
    }
}