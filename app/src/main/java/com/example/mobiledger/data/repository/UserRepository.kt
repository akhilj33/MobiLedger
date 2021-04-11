package com.example.mobiledger.data.repository

import com.example.mobiledger.data.sources.api.model.UserApi
import com.example.mobiledger.domain.entities.UserEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserRepository {
    suspend fun addUserToFirebaseDb(user: UserEntity)
}

class UserRepositoryImpl(private val userApi: UserApi, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : UserRepository {
    override suspend fun addUserToFirebaseDb(user: UserEntity) {
        withContext(dispatcher) {
            userApi.addUserToFirebaseDb(user)
        }
    }


}