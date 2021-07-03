package com.example.mobiledger.data.sources.auth

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.data.sources.room.MobiLedgerDatabase
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.domain.enums.SignInType
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await

interface AuthSource {
    suspend fun loginUserViaEmail(email: String, password: String): AppResult<UserEntity>
    suspend fun signUpViaEmail(name: String, phoneNo: String, email: String, password: String): AppResult<UserEntity>
    suspend fun signInViaGoogle(idToken: String?): AppResult<Pair<Boolean, UserEntity>>
    suspend fun isUserAuthorized(): Boolean
    suspend fun logOut(): AppResult<Boolean>
    suspend fun getCurrentUser(): FirebaseUser?
    suspend fun sendPasswordResetEmail(email: String): AppResult<Unit>
}

class AuthSourceImpl(
    private val firebaseAuth: FirebaseAuth, private val mobiLedgerDatabase: MobiLedgerDatabase
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
                    val signInType = SignInType.Email
                    AppResult.Success(authResultEntityMapper(result.data.result?.user as FirebaseUser, signInType))
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
                    AppResult.Success(signUpResultEntityMapper(result.data.result?.user as FirebaseUser, name, phoneNo, SignInType.Email))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun signInViaGoogle(idToken: String?): AppResult<Pair<Boolean, UserEntity>> {
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
                if (result.data != null && result.data.result != null && result.data.result?.user != null && result.data.result?.additionalUserInfo != null) {
                    val isNewUser = result.data.result?.additionalUserInfo!!.isNewUser
                    val signInType = SignInType.Google
                    val photo = result.data.result?.additionalUserInfo?.profile?.get("picture")
                    val userEntity = authResultEntityMapper(result.data.result?.user as FirebaseUser, signInType, photo)
                    AppResult.Success(Pair(isNewUser, userEntity))
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
                    mobiLedgerDatabase.clearAllTables()
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

    override suspend fun sendPasswordResetEmail(email: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            response = firebaseAuth.sendPasswordResetEmail(email)
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) {
                    AppResult.Success(Unit)
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> AppResult.Failure(result.error)
        }
    }
}

/*----------------------------------------Entity Mappers------------------------------------------------*/

private fun authResultEntityMapper(user: FirebaseUser, signInType: SignInType, photo: Any? = null): UserEntity {
    user.apply {
        return UserEntity(uid, displayName, photo?.toString(), email, phoneNumber, signInType)
    }
}

private fun signUpResultEntityMapper(user: FirebaseUser, name: String, phoneNo: String, signInType: SignInType): UserEntity {
    user.apply {
        return UserEntity(uid, name, photoUrl?.toString(), email, phoneNo, signInType)
    }
}
