package com.example.mobiledger.data.sources.api

import android.net.Uri
import com.example.mobiledger.common.utils.ConstantUtils.EMAIL_ID
import com.example.mobiledger.common.utils.ConstantUtils.PHONE_NUMBER
import com.example.mobiledger.common.utils.ConstantUtils.PHOTO_URI
import com.example.mobiledger.common.utils.ConstantUtils.UNAUTHORIZED_ERROR_MSG
import com.example.mobiledger.common.utils.ConstantUtils.USERS
import com.example.mobiledger.common.utils.ConstantUtils.USER_NAME
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.data.sources.auth.AuthSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.UserEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

interface UserApi {
    suspend fun addUserToFirebaseDb(user: UserEntity): AppResult<Unit>
    suspend fun fetchUserDataFromFirebaseDb(uid: String): AppResult<UserEntity>
    suspend fun updateUserNameInAuth(userName: String, uid: String): AppResult<Unit>
    suspend fun updatePhotoInAuth(photoUri: Uri, uid: String): AppResult<Uri>
    suspend fun updateEmailInAuth(email: String, uid: String): AppResult<Unit>
    suspend fun updateContactInFirebaseDB(contact: String, uid: String): AppResult<Unit>
    suspend fun updatePasswordInAuth(password: String): AppResult<Unit>
}

class UserApiImpl(private val firebaseDb: FirebaseFirestore, private val authSource: AuthSource, private val storage: StorageReference) :
    UserApi {

    override suspend fun addUserToFirebaseDb(user: UserEntity): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(ErrorCodes.FIREBASE_UNAUTHORIZED, UNAUTHORIZED_ERROR_MSG)
            response = firebaseDb.collection(USERS).document(user.uid).set(user)
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun fetchUserDataFromFirebaseDb(uid: String): AppResult<UserEntity> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(ErrorCodes.FIREBASE_UNAUTHORIZED, UNAUTHORIZED_ERROR_MSG)
            response = firebaseDb.collection(USERS).document(uid).get()
            response.await()

        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                val userInfo = userResultEntityMapper(result.data?.result)
                if (userInfo != null) {
                    AppResult.Success(userInfo)
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateUserNameInAuth(userName: String, uid: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(ErrorCodes.FIREBASE_UNAUTHORIZED, UNAUTHORIZED_ERROR_MSG)
            val user = authSource.getCurrentUser()
            val userProfileChangeRequest = UserProfileChangeRequest.Builder()
            userProfileChangeRequest.displayName = userName
            response = user?.updateProfile(userProfileChangeRequest.build())
            response?.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                val isUpdated = updateUserNameInDB(userName, uid)
                if (isUpdated) AppResult.Success(Unit)
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }


    private suspend fun updateUserNameInDB(userName: String, uid: String): Boolean {
        return try {
            val docRef = firebaseDb.collection(USERS).document(uid)
            docRef
                .update(USER_NAME, userName).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updatePhotoInAuth(photoUri: Uri, uid: String): AppResult<Uri> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(ErrorCodes.FIREBASE_UNAUTHORIZED, UNAUTHORIZED_ERROR_MSG)
            val user = authSource.getCurrentUser()
            val userProfileChangeRequest = UserProfileChangeRequest.Builder()
            userProfileChangeRequest.photoUri = photoUri
            response = user?.updateProfile(userProfileChangeRequest.build())
            response?.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                val isUpdated = updateUserPhotoInDB(photoUri, uid)
                if (isUpdated) AppResult.Success(photoUri)
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }


    private suspend fun updateUserPhotoInDB(photoUri: Uri, uid: String): Boolean {
        return try {
            val docRef = firebaseDb.collection(USERS).document(uid)
            docRef.update(PHOTO_URI, photoUri.toString()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateEmailInAuth(email: String, uid: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(ErrorCodes.FIREBASE_UNAUTHORIZED, UNAUTHORIZED_ERROR_MSG)
            val user = authSource.getCurrentUser()
            response = user?.updateEmail(email)
            response?.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                val isUpdated = updateEmailInDB(email, uid)
                if (isUpdated) AppResult.Success(Unit)
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateContactInFirebaseDB(contact: String, uid: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(ErrorCodes.FIREBASE_UNAUTHORIZED, UNAUTHORIZED_ERROR_MSG)
            val docRef = firebaseDb.collection(USERS).document(uid)
            response = docRef.update(PHONE_NUMBER, contact)
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updatePasswordInAuth(password: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(ErrorCodes.FIREBASE_UNAUTHORIZED, UNAUTHORIZED_ERROR_MSG)
            val user = authSource.getCurrentUser()
            response = user?.updatePassword(password)
            response?.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    private suspend fun updateEmailInDB(email: String, uid: String): Boolean {
        return try {
            val docRef = firebaseDb.collection(USERS).document(uid)
            docRef.update(EMAIL_ID, email).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}

private fun userResultEntityMapper(user: DocumentSnapshot?): UserEntity? {
    return user?.toObject(UserEntity::class.java)
}


