package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.ProfileRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity

interface ProfileUseCase {
    suspend fun fetchUserFromFirebase(isPTR: Boolean = false): AppResult<UserEntity>
    suspend fun updateUserNameInFirebase(username: String): AppResult<Unit>
    suspend fun updateEmailInFirebase(email: String): AppResult<Unit>
    suspend fun updatePhoneInFirebase(phoneNo: String): AppResult<Unit>
    suspend fun updatePasswordInFirebase(password: String): AppResult<Unit>
}

class ProfileUseCaseImpl(private val profileRepository: ProfileRepository) : ProfileUseCase {
    override suspend fun fetchUserFromFirebase(isPTR: Boolean): AppResult<UserEntity> {
        return profileRepository.fetchUserFromFirebase(isPTR)
    }

    override suspend fun updateUserNameInFirebase(username: String): AppResult<Unit> {
        return profileRepository.updateUserNameInFirebase(username)
    }

    override suspend fun updateEmailInFirebase(email: String): AppResult<Unit> {
        return profileRepository.updateEmailInFirebase(email)
    }

    override suspend fun updatePhoneInFirebase(phoneNo: String): AppResult<Unit> {
        return profileRepository.updatePhoneNoInFirebase(phoneNo)
    }

    override suspend fun updatePasswordInFirebase(password: String): AppResult<Unit> {
        return profileRepository.updatePasswordInFirebase(password)
    }
}
