package com.example.mobiledger.data.repository

import com.example.mobiledger.common.listener.AuthListener
import com.example.mobiledger.data.sources.api.model.FirebaseAuthLoginUsingEmailApi
import com.example.mobiledger.domain.AppResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface AuthRepository {
    suspend fun loginUsingEmail(email : String, password : String)
}

class AuthRepositoryImpl(
//    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val firebaseAuthLoginUsingEmailApi: FirebaseAuthLoginUsingEmailApi
) : AuthRepository{
    override suspend fun loginUsingEmail(email: String, password: String) {
        firebaseAuthLoginUsingEmailApi.getUserFromFirebase(email, password)
    }
}