package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.UserRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity

interface UserUseCase {
    suspend fun addUserToFirebaseDb(user: UserEntity): AppResult<Unit>
}

class UserUseCaseImpl(private val userRepository: UserRepository) : UserUseCase {
    override suspend fun addUserToFirebaseDb(user: UserEntity): AppResult<Unit> = userRepository.addUserToFirebaseDb(user)

}