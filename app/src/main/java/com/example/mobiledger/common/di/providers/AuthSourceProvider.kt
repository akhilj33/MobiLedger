package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.api.model.AuthSource
import com.example.mobiledger.data.sources.api.model.AuthSourceImpl

class AuthSourceProvider(private val firebaseProvider: FirebaseProvider, private val apiSourceProvider: ApiSourceProvider) {
    fun provideAuthSource(): AuthSource = AuthSourceImpl(firebaseProvider.provideFirebaseAuth(), apiSourceProvider.provideUserApiSource())

}