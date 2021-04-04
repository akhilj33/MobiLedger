package com.example.mobiledger.data.sources.api.model

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.AuthEntity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

interface AuthSource {
    suspend fun loginUserViaEmail(email: String, password: String): AppResult<AuthEntity>
    suspend fun signUpViaEmail(email: String, password: String): AppResult<AuthEntity>
    suspend fun signInViaGoogle(idToken: String?): AppResult<AuthEntity>
    suspend fun getCurrentUser(): AppResult<AuthEntity>
    suspend fun logOut(): AppResult<Boolean>
}

class AuthSourceImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthSource {

    override suspend fun loginUserViaEmail(email: String, password: String): AppResult<AuthEntity> {
        var response: AuthResult? = null
        var exception: Exception? = null
        try {
            response = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.user != null) {
                    AppResult.Success(authResultEntityMapper(result.data.user!!))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun signUpViaEmail(email: String, password: String): AppResult<AuthEntity> {
        var response: AuthResult? = null
        var exception: Exception? = null
        try {
            response = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.user != null) {
                    //todo change parameter of this fn to user profile and update user profile here
                    AppResult.Success(authResultEntityMapper(result.data.user!!))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun signInViaGoogle(idToken: String?): AppResult<AuthEntity> {
        var response: AuthResult? = null
        var exception: Exception? = null
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        try {
            response = firebaseAuth.signInWithCredential(credential).await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.user != null) {
                    AppResult.Success(authResultEntityMapper(result.data.user!!))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun getCurrentUser(): AppResult<AuthEntity> {
        var response: FirebaseUser? = null
        var exception: Exception? = null
        try{
            response = firebaseAuth.currentUser
        } catch (e:Exception){
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) {
                    AppResult.Success(authResultEntityMapper(result.data))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun logOut(): AppResult<Boolean> {
        var response: Unit? = null
        var exception: Exception? = null
        try{
            response = firebaseAuth.signOut()
        } catch (e:Exception){
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
}

/*----------------------------------------Entity Mappers------------------------------------------------*/

private fun authResultEntityMapper(user: FirebaseUser): AuthEntity {
    user.apply {
        return AuthEntity(uid, displayName, photoUrl, email, phoneNumber)
    }
}

