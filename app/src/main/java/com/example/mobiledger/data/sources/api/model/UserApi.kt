package com.example.mobiledger.data.sources.api.model

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.domain.entities.UserInfoEntity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

const val USERS = "Users"
const val EMAIL_ID = "emailId"
const val USER_NAME = "userName"
const val PHONE_NUMBER = "phoneNo"

interface UserApi {
    suspend fun addUserToFirebaseDb(user: UserEntity): Boolean
    suspend fun fetchUserDataFromFirebaseDb(uid: String): AppResult<UserInfoEntity?>
    suspend fun updateUserNameInFirebase(userName: String, uid: String): Boolean
    suspend fun updateEmailInFirebase(email: String, uid: String): Boolean
    suspend fun updateContactInFirebaseDB(contact: String, uid: String): Boolean
    suspend fun updatePasswordInFirebase(password: String): Boolean
}

class UserApiImpl(private val firebaseDb: FirebaseFirestore) : UserApi {

    override suspend fun addUserToFirebaseDb(user: UserEntity): Boolean {
        return try {
            firebaseDb.collection(USERS).document(user.uid!!).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun fetchUserDataFromFirebaseDb(uid: String): AppResult<UserInfoEntity?> {
        var response: DocumentSnapshot? = null
        var exception: Exception? = null
        try {
            val docRef = firebaseDb.collection(USERS).document(uid)
            response = docRef.get().await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) {
                    AppResult.Success(userResultEntityMapper(result.data))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateUserNameInFirebase(userName: String, uid: String): Boolean {
        return try {
            //todo
//            val user = Firebase.auth.currentUser
//            val profileUpdates = userProfileChangeRequest {
//                displayName = userName
//                photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
//            }
//            user?.updateProfile(profileUpdates)?.await()
            updateUserNameInDB(userName, uid)
        } catch (e: Exception) {
            false
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

    override suspend fun updateEmailInFirebase(email: String, uid: String): Boolean {
        return try {
            val user = Firebase.auth.currentUser
            user?.updateEmail(email)?.await()
            updateEmailInDB(email, uid)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateContactInFirebaseDB(contact: String, uid: String): Boolean {
        return try {
            val docRef = firebaseDb.collection(USERS).document(uid)
            docRef
                .update(PHONE_NUMBER, contact).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updatePasswordInFirebase(password: String): Boolean {
        return try {
            val user = Firebase.auth.currentUser
            user?.updatePassword(password)?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun updateEmailInDB(email: String, uid: String): Boolean {
        return try {
            val docRef = firebaseDb.collection(USERS).document(uid)
            docRef
                .update(EMAIL_ID, email).await()
            true
        } catch (e: java.lang.Exception) {
            false
        }
    }
}

private fun userResultEntityMapper(user: DocumentSnapshot?): UserInfoEntity? {
    user.apply {
        return user?.toObject(UserInfoEntity::class.java)
    }
}