package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.api.TransactionApi
import com.example.mobiledger.data.sources.api.TransactionApiImpl
import com.example.mobiledger.data.sources.api.UserApi
import com.example.mobiledger.data.sources.api.UserApiImpl

/**
 * Provides Constructor dependencies to all api sources present in app
 **/

class ApiSourceProvider(
    private val retrofitProvider: RetrofitProvider,
    private val firebaseProvider: FirebaseProvider,
    private val authSourceProvider: AuthSourceProvider
) {
    fun provideUserApiSource(): UserApi = UserApiImpl(firebaseProvider.provideFirebaseDatabase(), authSourceProvider.provideAuthSource())
    fun provideTransactionApiSource(): TransactionApi =
        TransactionApiImpl(firebaseProvider.provideFirebaseDatabase(), authSourceProvider.provideAuthSource())

}