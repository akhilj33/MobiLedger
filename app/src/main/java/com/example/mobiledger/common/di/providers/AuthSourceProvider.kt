package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.auth.AuthSource
import com.example.mobiledger.data.sources.auth.AuthSourceImpl

class AuthSourceProvider(private val firebaseProvider: FirebaseProvider) {
    fun provideAuthSource(): AuthSource = AuthSourceImpl(firebaseProvider.provideFirebaseAuth())

}