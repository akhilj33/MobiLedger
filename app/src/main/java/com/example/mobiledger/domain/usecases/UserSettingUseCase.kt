package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.UserSettingsRepository

interface UserSettingsUseCase {
    suspend fun getUID(): String?
    suspend fun saveUID(uid: String?)
}

class UserSettingsUseCaseImpl(
    private val userSettingsRepository: UserSettingsRepository
) : UserSettingsUseCase {
    override suspend fun getUID(): String? {
        return userSettingsRepository.getUID()
    }

    override suspend fun saveUID(uid: String?) {
        userSettingsRepository.saveUID(uid)
    }
}