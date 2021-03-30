package com.example.mobiledger

import android.app.Application
import com.example.mobiledger.common.di.DependencyProvider

class BaseApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        DependencyProvider.inject(this)
    }
}