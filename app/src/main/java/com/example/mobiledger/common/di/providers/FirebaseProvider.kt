package com.example.mobiledger.common.di.providers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FirebaseProvider {
    fun provideFirebaseAuth(): FirebaseAuth =  FirebaseAuth.getInstance()
    fun provideFirebaseDatabase(): FirebaseFirestore = Firebase.firestore
    fun provideFirebaseStorageReference(): StorageReference = Firebase.storage.reference
}