package com.example.mobiledger.data.repository

import android.net.Uri
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.api.UserApi
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.data.sources.room.profile.ProfileDb
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ProfileRepository {
    suspend fun fetchUserFromFirebase(isPTR: Boolean): AppResult<UserEntity>
    suspend fun updateUserNameInFirebase(username: String): AppResult<Unit>
    suspend fun updateEmailInFirebase(email: String): AppResult<Unit>
    suspend fun updatePhoneNoInFirebase(phoneNo: String): AppResult<Unit>
    suspend fun updatePhotoInAuth(photoUri: Uri): AppResult<Uri>
    suspend fun deletePhotoInAuth(): AppResult<Unit>
}

class ProfileRepositoryImpl(
    private val userApi: UserApi, private val cacheSource: CacheSource, private val profileDb: ProfileDb,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProfileRepository {

    override suspend fun fetchUserFromFirebase(isPTR: Boolean): AppResult<UserEntity> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                val userExists = profileDb.hasUser()
                if (!userExists || isPTR) {
                    when (val firebaseResult = userApi.fetchUserDataFromFirebaseDb(uId)) {
                        is AppResult.Success -> {
                            profileDb.saveUser(firebaseResult.data)
                        }
                        is AppResult.Failure -> {
                            return@withContext firebaseResult
                        }
                    }
                }
                profileDb.fetchUserProfile()
            } else {
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
        }
    }

    override suspend fun updateUserNameInFirebase(username: String): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) userApi.updateUserNameInAuth(username, uId).also {
                if (it is AppResult.Success) profileDb.updateUserName(username, uId)
            }
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updateEmailInFirebase(email: String): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) userApi.updateEmailInAuth(email, uId).also {
                if (it is AppResult.Success) profileDb.updateEmailId(email, uId)
            }
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updatePhoneNoInFirebase(phoneNo: String): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) userApi.updateContactInFirebaseDB(phoneNo, uId).also {
                if (it is AppResult.Success) profileDb.updatePhoneNo(phoneNo, uId)
            }
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updatePhotoInAuth(photoUri: Uri): AppResult<Uri> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) userApi.updatePhotoInAuth(photoUri, uId).also {
                if (it is AppResult.Success) profileDb.updatePhoto(photoUri, uId)
            }
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun deletePhotoInAuth(): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) userApi.deletePhotoInAuth(uId).also {
                if (it is AppResult.Success) profileDb.updatePhoto(null, uId)
            }
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }
}