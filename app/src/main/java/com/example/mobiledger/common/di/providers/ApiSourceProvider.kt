package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.api.model.AuthSource
import com.example.mobiledger.data.sources.api.model.AuthSourceImpl

/**
 * Provides Constructor dependencies to all api sources present in app
 **/

class ApiSourceProvider(
    private val retrofitProvider: RetrofitProvider,
    private val firebaseAuthProvider: FirebaseAuthProvider
) {

    fun provideFirebaseAuth(): AuthSource = AuthSourceImpl(firebaseAuthProvider.provideFirebaseAuth())
}