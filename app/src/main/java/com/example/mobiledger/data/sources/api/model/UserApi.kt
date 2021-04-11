package com.example.mobiledger.data.sources.api.model

import com.example.mobiledger.domain.entities.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface UserApi {
    suspend fun addUserToFirebaseDb(user: UserEntity): Boolean
}

class UserApiImpl(private val firebaseDb: FirebaseFirestore) : UserApi {

    override suspend fun addUserToFirebaseDb(user: UserEntity): Boolean {
        return try {
            firebaseDb.collection("Users").document(user.uId).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
