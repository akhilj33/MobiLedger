package com.example.mobiledger.data.repository

import com.example.mobiledger.data.sources.cache.CacheSource

interface UserSettingsRepository {
    suspend fun getUID(): String?
    suspend fun saveUID(uid: String)
    suspend fun saveBiometricEnable(isEnable: Boolean)
    suspend fun isBiometricEnable(): Boolean
    suspend fun saveNotificationEnable(isEnable: Boolean)
    suspend fun isNotificationEnable(): Boolean
    suspend fun saveReminderEnable(isEnable: Boolean)
    suspend fun isReminderEnable(): Boolean
    suspend fun clearDataOnLogout()
    suspend fun isTermsAndConditionAccepted(): Boolean
    suspend fun acceptTermsAndCondition(isAccepted: Boolean)
}

class UserSettingsRepositoryImpl(
    private val cacheSource: CacheSource
) :
    UserSettingsRepository {

    override suspend fun getUID(): String? {
        return cacheSource.getUID()
    }

    override suspend fun saveUID(uid: String) {
        cacheSource.saveUid(uid)
    }

    override suspend fun saveBiometricEnable(isEnable: Boolean) {
        cacheSource.saveBiometricEnable(isEnable)
    }

    override suspend fun isBiometricEnable(): Boolean {
        return cacheSource.isBiometricEnable()
    }

    override suspend fun saveNotificationEnable(isEnable: Boolean) {
        cacheSource.saveNotificationEnable(isEnable)
    }

    override suspend fun isNotificationEnable(): Boolean {
        return cacheSource.isNotificationEnable()
    }

    override suspend fun saveReminderEnable(isEnable: Boolean) {
        cacheSource.saveReminderEnable(isEnable)
    }

    override suspend fun isReminderEnable(): Boolean {
        return cacheSource.isReminderEnable()
    }

    override suspend fun clearDataOnLogout() {
        cacheSource.clearSharedPreferenceOnLogout()
    }

    override suspend fun isTermsAndConditionAccepted(): Boolean {
        return cacheSource.isTermsAndConditionAccepted()
    }

    override suspend fun acceptTermsAndCondition(isAccepted: Boolean) {
        cacheSource.acceptTermsAndCondition(isAccepted)
    }
}
