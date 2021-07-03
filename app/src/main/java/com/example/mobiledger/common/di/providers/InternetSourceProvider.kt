package com.example.mobiledger.common.di.providers

import android.content.Context
import com.example.mobiledger.data.sources.internet.InternetSource
import com.example.mobiledger.data.sources.internet.InternetSourceImpl

class InternetSourceProvider(context: Context) {
    private val internetSource: InternetSource = InternetSourceImpl(context)
    fun provideInternetSource(): InternetSource = internetSource
}
