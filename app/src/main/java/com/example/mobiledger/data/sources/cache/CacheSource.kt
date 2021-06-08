package com.example.mobiledger.data.sources.cache

import android.content.Context
import android.content.SharedPreferences

interface CacheSource {

    suspend fun saveUid(uid: String)
    suspend fun getUID(): String?
    suspend fun saveBiometricEnable(isEnable: Boolean)
    suspend fun isBiometricEnable(): Boolean
    suspend fun saveNotificationEnable(isEnable: Boolean)
    suspend fun isNotificationEnable(): Boolean
    suspend fun saveReminderEnable(isEnable: Boolean)
    suspend fun isReminderEnable(): Boolean
    suspend fun removeUid()
}

class SharedPreferenceSource(val context: Context) : CacheSource {

    companion object {
        private const val PREFS_NAME = "MOBI_LEDGER_PREF"
        private const val UID = "uid"
        private const val NOTIFICATION = "notification"
        private const val BIOMETRIC = "biometric"
        private const val REMINDER = "reminder"
    }

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun saveUid(uid: String) {
        sharedPref.edit().putString(UID, uid).apply()
    }

    override suspend fun getUID(): String? {
        return sharedPref.getString(UID, null)
    }

    override suspend fun saveBiometricEnable(isEnable: Boolean) {
        sharedPref.edit().putBoolean(BIOMETRIC, isEnable).apply()
    }

    override suspend fun isBiometricEnable(): Boolean {
        return sharedPref.getBoolean(BIOMETRIC, false)
    }

    override suspend fun saveNotificationEnable(isEnable: Boolean) {
        sharedPref.edit().putBoolean(NOTIFICATION, isEnable).apply()
    }

    override suspend fun isNotificationEnable(): Boolean {
        return sharedPref.getBoolean(NOTIFICATION, true)
    }

    override suspend fun saveReminderEnable(isEnable: Boolean) {
        sharedPref.edit().putBoolean(REMINDER, isEnable).apply()
    }

    override suspend fun isReminderEnable(): Boolean {
        return sharedPref.getBoolean(REMINDER, false)
    }

    override suspend fun removeUid() {
        sharedPref.edit().remove(UID).apply()
    }

}