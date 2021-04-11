package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.api.model.AuthSource
import com.example.mobiledger.data.sources.api.model.AuthSourceImpl
import com.example.mobiledger.data.sources.api.model.UserApi
import com.example.mobiledger.data.sources.api.model.UserApiImpl

/**
 * Provides Constructor dependencies to all api sources present in app
 **/

class ApiSourceProvider(
    private val retrofitProvider: RetrofitProvider,
    private val firebaseProvider: FirebaseProvider
) {
    fun provideUserApiSource(): UserApi = UserApiImpl(firebaseProvider.provideFirebaseDatabase())

}