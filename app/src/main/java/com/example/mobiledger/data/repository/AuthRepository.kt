package com.example.mobiledger.data.repository

import com.example.mobiledger.data.sources.api.model.AuthSource
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface AuthRepository {
    suspend fun loginUsingEmail(email: String, password: String): AppResult<UserEntity>
    suspend fun signUpViaEmail(name: String, phoneNo: String, email: String, password: String): AppResult<UserEntity>
    suspend fun signInViaGoogle(idToken: String?): AppResult<UserEntity>
    suspend fun isUserAuthorized(): Boolean
    suspend fun logOut(): AppResult<Boolean>
}

class AuthRepositoryImpl(
    private val authSource: AuthSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {
    override suspend fun loginUsingEmail(email: String, password: String): AppResult<UserEntity> {
        return withContext(dispatcher) {
            authSource.loginUserViaEmail(email, password)
        }
    }

    override suspend fun signUpViaEmail(name: String, phoneNo: String, email: String, password: String): AppResult<UserEntity> {
        return withContext(dispatcher) {
            authSource.signUpViaEmail(name, phoneNo, email, password)
        }
    }

    override suspend fun signInViaGoogle(idToken: String?): AppResult<UserEntity> {
        return withContext(dispatcher) {
            authSource.signInViaGoogle(idToken)
        }
    }

    override suspend fun isUserAuthorized(): Boolean {
        return withContext(dispatcher) {
            authSource.isUserAuthorized()
        }
    }

    override suspend fun logOut(): AppResult<Boolean> {
        return withContext(dispatcher) {
            authSource.logOut()
        }
    }
}