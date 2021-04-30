package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.ProfileRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserInfoEntity

interface ProfileUseCase {
    suspend fun fetchUserFromFirestoreDb(uid: String): AppResult<UserInfoEntity?>
    suspend fun updateUserNameInFirebase(username: String, uid: String): AppResult<Unit>
    suspend fun updateEmailInFirebase(email: String, uid: String): AppResult<Unit>
    suspend fun updatePhoneInFirebaseDB(phoneNo: String, uid: String): AppResult<Unit>
    suspend fun updatePasswordInFirebase(password: String): AppResult<Unit>
}

class ProfileUseCaseImpl(private val profileRepository: ProfileRepository) : ProfileUseCase {
    override suspend fun fetchUserFromFirestoreDb(uid: String): AppResult<UserInfoEntity?> {
        return profileRepository.fetchUserFromFirestoreDb(uid)
    }

    override suspend fun updateUserNameInFirebase(username: String, uid: String): AppResult<Unit> {
        return profileRepository.updateUserNameInFirebase(username, uid)
    }

    override suspend fun updateEmailInFirebase(email: String, uid: String): AppResult<Unit> {
        return profileRepository.updateEmailInFirebase(email, uid)
    }

    override suspend fun updatePhoneInFirebaseDB(phoneNo: String, uid: String): AppResult<Unit> {
        return profileRepository.updatePhoneNoInFirebaseDB(phoneNo, uid)
    }

    override suspend fun updatePasswordInFirebase(password: String): AppResult<Unit> {
        return profileRepository.updatePasswordInFirebase(password)
    }
}
