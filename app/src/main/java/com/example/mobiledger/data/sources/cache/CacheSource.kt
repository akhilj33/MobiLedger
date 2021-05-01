package com.example.mobiledger.data.sources.cache

import android.content.Context
import android.content.SharedPreferences

interface CacheSource {

    suspend fun saveUid(uid: String)
    suspend fun getUID(): String?
}

class SharedPreferenceSource(val context: Context) : CacheSource {

    companion object {
        private const val PREFS_NAME = "MOBI_LEDGER_PREF"
        private const val UID = "uid"
    }

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun saveUid(uid: String) {
        sharedPref.edit().putString(UID, uid).apply()
    }

    override suspend fun getUID(): String? {
        return sharedPref.getString(UID, null)
    }
}