package com.example.mobiledger.data.repository

import com.example.mobiledger.data.sources.cache.CacheSource

interface UserSettingsRepository {
    suspend fun getUID(): String?
    suspend fun saveUID(uid: String)
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
}
