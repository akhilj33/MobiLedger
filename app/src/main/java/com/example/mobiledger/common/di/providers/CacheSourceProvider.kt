package com.example.mobiledger.common.di.providers

import android.content.Context
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.data.sources.cache.SharedPreferenceSource

/**
 * Provides Constructor dependencies to cache source to manage shared preferences
 **/

class CacheSourceProvider(context: Context) {
    private val sharedPreferenceSource by lazy { SharedPreferenceSource(context) }
    fun provideCacheSource(): CacheSource = sharedPreferenceSource
}

