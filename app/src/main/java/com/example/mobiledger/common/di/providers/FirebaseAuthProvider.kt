package com.example.mobiledger.common.di.providers

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthProvider {
    fun provideFirebaseAuth(): FirebaseAuth =  FirebaseAuth.getInstance()
}