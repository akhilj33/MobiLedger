package com.example.mobiledger.data.repository

import com.example.mobiledger.data.sources.api.model.UserApi
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserInfoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ProfileRepository {
    suspend fun fetchUserFromFirestoreDb(uid: String): AppResult<UserInfoEntity?>
    suspend fun updateUserNameInFirebase(username: String, uid: String): AppResult<Unit>
    suspend fun updateEmailInFirebase(email: String, uid: String): AppResult<Unit>
    suspend fun updatePhoneNoInFirebaseDB(phoneNo: String, uid: String): AppResult<Unit>
    suspend fun updatePasswordInFirebase(password: String): AppResult<Unit>
}

class ProfileRepositoryImpl(private val userApi: UserApi, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    ProfileRepository {

    override suspend fun fetchUserFromFirestoreDb(uid: String): AppResult<UserInfoEntity?> {
        return withContext(dispatcher) {
            userApi.fetchUserDataFromFirebaseDb(uid)
        }
    }

    override suspend fun updateUserNameInFirebase(username: String, uid: String): AppResult<Unit> {
        return withContext(dispatcher) {
            userApi.updateUserNameInAuth(username, uid)
        }
    }

    override suspend fun updateEmailInFirebase(email: String, uid: String): AppResult<Unit> {
        return withContext(dispatcher) {
            userApi.updateEmailInAuth(email, uid)
        }
    }

    override suspend fun updatePhoneNoInFirebaseDB(phoneNo: String, uid: String): AppResult<Unit> {
        return withContext(dispatcher) {
            userApi.updateContactInFirebaseDB(phoneNo, uid)
        }
    }

    override suspend fun updatePasswordInFirebase(password: String): AppResult<Unit> {
        return withContext(dispatcher) {
            userApi.updatePasswordInAuth(password)
        }
    }
}