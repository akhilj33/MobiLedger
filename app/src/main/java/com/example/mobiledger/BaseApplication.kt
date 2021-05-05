package com.example.mobiledger

import android.app.Application
import com.example.mobiledger.common.di.DependencyProvider
import timber.log.Timber

class BaseApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        DependencyProvider.inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}