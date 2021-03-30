package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.api.model.FirebaseAuthLoginUsingEmailApi
import com.example.mobiledger.data.sources.api.model.FirebaseAuthLoginUsingEmailApiImpl

/**
 * Provides Constructor dependencies to all api sources present in app
 **/

class ApiSourceProvider(private val retrofitProvider: RetrofitProvider,
                        private val firebaseAuthProvider: FirebaseAuthProvider ) {

    fun provideFirebaseAuth(): FirebaseAuthLoginUsingEmailApi =
        FirebaseAuthLoginUsingEmailApiImpl(
            firebaseAuthProvider.provideFirebaseAuth()
        )
}