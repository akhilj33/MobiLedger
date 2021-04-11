package com.example.mobiledger.common.di.providers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseProvider {
    fun provideFirebaseAuth(): FirebaseAuth =  FirebaseAuth.getInstance()
    fun provideFirebaseDatabase(): FirebaseFirestore = Firebase.firestore
}