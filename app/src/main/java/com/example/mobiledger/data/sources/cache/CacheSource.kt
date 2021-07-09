package com.example.mobiledger.data.sources.cache

import android.content.Context
import android.content.SharedPreferences
import com.example.mobiledger.common.utils.JsonUtils.convertJsonStringToObject
import com.example.mobiledger.common.utils.JsonUtils.convertToJsonString

interface CacheSource {
    suspend fun saveUid(uid: String)
    suspend fun getUID(): String?
    suspend fun saveBiometricEnable(isEnable: Boolean)
    suspend fun isBiometricEnable(): Boolean
    suspend fun saveNotificationEnable(isEnable: Boolean)
    suspend fun isNotificationEnable(): Boolean
    suspend fun saveReminderEnable(isEnable: Boolean)
    suspend fun isReminderEnable(): Boolean
    suspend fun clearSharedPreferenceOnLogout()
    suspend fun isTermsAndConditionAccepted(): Boolean
    suspend fun acceptTermsAndCondition(isAccepted: Boolean)
    suspend fun setIsFirstTimePermissionAsked(permissions: Array<String>)
    suspend fun isFirstTimePermissionAsked(permissions: Array<String>): Boolean
}

class SharedPreferenceSource(val context: Context) : CacheSource {

    companion object {
        private const val PREFS_NAME = "MOBI_LEDGER_PREF"
        private const val UID = "uid"
        private const val NOTIFICATION = "notification"
        private const val BIOMETRIC = "biometric"
        private const val REMINDER = "reminder"
        private const val T_AND_C = "tAndC"
        private const val PERMISSIONS_IS_FIRST_TIME = "permission_is_first_time"
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

    override suspend fun clearSharedPreferenceOnLogout() {
        sharedPref.edit().clear().apply()
        acceptTermsAndCondition(true)
    }

    override suspend fun isTermsAndConditionAccepted(): Boolean {
        return sharedPref.getBoolean(T_AND_C, false)
    }

    override suspend fun acceptTermsAndCondition(isAccepted: Boolean) {
        sharedPref.edit().putBoolean(T_AND_C, isAccepted).apply()
    }

    override suspend fun setIsFirstTimePermissionAsked(permissions: Array<String>) {
        val permissionsJson = sharedPref.getString(PERMISSIONS_IS_FIRST_TIME, "")
        val permissionMap: MutableMap<String, Boolean> = convertJsonStringToObject(permissionsJson) ?: mutableMapOf()
        permissions.forEach { permissionMap[it] = false }
        sharedPref.edit().putString(PERMISSIONS_IS_FIRST_TIME, convertToJsonString(permissionMap)).apply()
    }

    override suspend fun isFirstTimePermissionAsked(permissions: Array<String>): Boolean {
        val permissionsJson = sharedPref.getString(PERMISSIONS_IS_FIRST_TIME, "")
        val permissionMap: MutableMap<String, Boolean> = convertJsonStringToObject(permissionsJson) ?: mutableMapOf()
        permissions.forEach {
            if (permissionMap.containsKey(it)) {
                return false
            }
        }
        return true
    }

}