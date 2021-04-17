package com.example.mobiledger.data.repository

import com.example.mobiledger.data.sources.api.model.UserApi
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserInfoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ProfileRepository {
    suspend fun fetchUserFromFirestoreDb(uid: String): AppResult<UserInfoEntity?>
    suspend fun updateUserNameInFirebase(username: String, uid: String): Boolean
    suspend fun updateEmailInFirebase(email: String, uid: String): Boolean
    suspend fun updatePhoneNoInFirebaseDB(phoneNo: String, uid: String): Boolean
    suspend fun updatePasswordInFirebase(password: String): Boolean
}

class ProfileRepositoryImpl(private val userApi: UserApi, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    ProfileRepository {

    override suspend fun fetchUserFromFirestoreDb(uid: String): AppResult<UserInfoEntity?> {
        return withContext(dispatcher) {
            userApi.fetchUserDataFromFirebaseDb(uid)
        }
    }

    override suspend fun updateUserNameInFirebase(username: String, uid: String): Boolean {
        return withContext(dispatcher) {
            userApi.updateUserNameInFirebase(username, uid)
        }
    }

    override suspend fun updateEmailInFirebase(email: String, uid: String): Boolean {
        return withContext(dispatcher) {
            userApi.updateEmailInFirebase(email, uid)
        }
    }

    override suspend fun updatePhoneNoInFirebaseDB(phoneNo: String, uid: String): Boolean {
        return withContext(dispatcher) {
            userApi.updateContactInFirebaseDB(phoneNo, uid)
        }
    }

    override suspend fun updatePasswordInFirebase(password: String): Boolean {
        return withContext(dispatcher) {
            userApi.updatePasswordInFirebase(password)
        }
    }
}