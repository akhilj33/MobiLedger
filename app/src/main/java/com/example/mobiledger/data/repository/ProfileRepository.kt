package com.example.mobiledger.data.repository

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.api.UserApi
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserInfoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ProfileRepository {
    suspend fun fetchUserFromFirebase(): AppResult<UserInfoEntity>
    suspend fun updateUserNameInFirebase(username: String): AppResult<Unit>
    suspend fun updateEmailInFirebase(email: String): AppResult<Unit>
    suspend fun updatePhoneNoInFirebase(phoneNo: String): AppResult<Unit>
    suspend fun updatePasswordInFirebase(password: String): AppResult<Unit>
}

class ProfileRepositoryImpl(
    private val userApi: UserApi, private val cacheSource: CacheSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProfileRepository {

    override suspend fun fetchUserFromFirebase(): AppResult<UserInfoEntity> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) userApi.fetchUserDataFromFirebaseDb(uId)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updateUserNameInFirebase(username: String): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) userApi.updateUserNameInAuth(username, uId)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updateEmailInFirebase(email: String): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) userApi.updateEmailInAuth(email, uId)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updatePhoneNoInFirebase(phoneNo: String): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) userApi.updateContactInFirebaseDB(phoneNo, uId)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updatePasswordInFirebase(password: String): AppResult<Unit> {
        return withContext(dispatcher) {
            userApi.updatePasswordInAuth(password)
        }
    }
}