package com.example.mobiledger.data.sources.auth

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.UserEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await

interface AuthSource {
    suspend fun loginUserViaEmail(email: String, password: String): AppResult<UserEntity>
    suspend fun signUpViaEmail(name: String, phoneNo: String, email: String, password: String): AppResult<UserEntity>
    suspend fun signInViaGoogle(idToken: String?): AppResult<UserEntity>
    suspend fun isUserAuthorized(): Boolean
    suspend fun logOut(): AppResult<Boolean>
    suspend fun getCurrentUser(): FirebaseUser?
}

class AuthSourceImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthSource {

    override suspend fun loginUserViaEmail(email: String, password: String): AppResult<UserEntity> {
        var response: Task<AuthResult>? = null
        var exception: Exception? = null
        try {
            response = firebaseAuth.signInWithEmailAndPassword(email, password)
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null && result.data.result?.user != null) {
                    AppResult.Success(authResultEntityMapper(result.data.result?.user as FirebaseUser))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun signUpViaEmail(name: String, phoneNo: String, email: String, password: String): AppResult<UserEntity> {
        var response: Task<AuthResult>? = null
        var exception: Exception? = null
        try {
            response = firebaseAuth.createUserWithEmailAndPassword(email, password)
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null && result.data.result?.user != null) {
                    AppResult.Success(signUpResultEntityMapper(result.data.result?.user as FirebaseUser, name, phoneNo))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun signInViaGoogle(idToken: String?): AppResult<UserEntity> {
        var response: Task<AuthResult>? = null
        var exception: Exception? = null
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        try {
            response = firebaseAuth.signInWithCredential(credential)
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null && result.data.result?.user != null) {
                    AppResult.Success(authResultEntityMapper(result.data.result?.user as FirebaseUser))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun isUserAuthorized(): Boolean {
        var response: Task<GetTokenResult>? = null
        var exception: Exception? = null
        try {
            response = firebaseAuth.currentUser?.getIdToken(true)
            response?.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                result.data?.result != null
            }
            is FireBaseResult.Failure -> false
        }
    }

    override suspend fun logOut(): AppResult<Boolean> {
        var response: Unit? = null
        var exception: Exception? = null
        try {
            response = firebaseAuth.signOut()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) {
                    AppResult.Success(true)
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}

/*----------------------------------------Entity Mappers------------------------------------------------*/

private fun authResultEntityMapper(user: FirebaseUser): UserEntity {
    user.apply {
        return UserEntity(uid, displayName, photoUrl, email, phoneNumber)
    }
}

private fun signUpResultEntityMapper(user: FirebaseUser, name: String, phoneNo: String): UserEntity {
    user.apply {
        return UserEntity(uid, name, photoUrl, email, phoneNo)
    }
}
