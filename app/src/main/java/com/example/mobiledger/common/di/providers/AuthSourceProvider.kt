package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.auth.AuthSource
import com.example.mobiledger.data.sources.auth.AuthSourceImpl
import com.example.mobiledger.data.sources.room.MobiLedgerDatabase

class AuthSourceProvider(private val firebaseProvider: FirebaseProvider, private val mobiledgerDatabaseProvider: MobiLedgerDatabase) {
    fun provideAuthSource(): AuthSource = AuthSourceImpl(firebaseProvider.provideFirebaseAuth(), mobiledgerDatabaseProvider)

}