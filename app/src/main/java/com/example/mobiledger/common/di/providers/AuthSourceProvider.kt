package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.api.model.AuthSource
import com.example.mobiledger.data.sources.api.model.AuthSourceImpl

class AuthSourceProvider(private val firebaseProvider: FirebaseProvider) {
    fun provideAuthSource(): AuthSource = AuthSourceImpl(firebaseProvider.provideFirebaseAuth())

}