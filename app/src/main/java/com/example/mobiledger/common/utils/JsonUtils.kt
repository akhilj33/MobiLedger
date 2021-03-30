package com.example.mobiledger.common.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber

object JsonUtils {

    fun convertToJsonString(data: Any?): String? {
        if (data != null) {
            try {
                return Gson().toJson(data)
            } catch (e: Exception) {
                Timber.e("Exception occurred while converting data to json string")
            }
        }
        return null
    }

    inline fun <reified T> convertJsonStringToObject(jsonString: String?): T? {
        if (!jsonString.isNullOrEmpty()) {
            try {
                return Gson().fromJson(jsonString, object : TypeToken<T>() {}.type)
            } catch (e: Exception) {
                Timber.e("Exception occurred while converting parsing json string to Class")
            }
        }
        return null
    }
}