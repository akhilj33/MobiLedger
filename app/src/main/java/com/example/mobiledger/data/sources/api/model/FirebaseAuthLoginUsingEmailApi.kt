package com.example.mobiledger.data.sources.api.model

import android.app.Activity
import com.example.mobiledger.common.listener.AuthListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthLoginUsingEmailApi {
    suspend fun getUserFromFirebase(
        email: String,
        password: String,
//        listener: AuthListener<FirebaseUser?>
    )
}

class FirebaseAuthLoginUsingEmailApiImpl(
    private val firebaseAuth : FirebaseAuth
) : FirebaseAuthLoginUsingEmailApi {

    override suspend fun getUserFromFirebase(
        email: String,
        password: String,
//        listener: AuthListener<FirebaseUser?>
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(Activity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val userName = firebaseAuth.currentUser?.displayName ?: ""
                    val responseData = firebaseAuth.currentUser
//                    listener.onResponse(responseData!!)
                } else {
                    // If sign in fails, display a message to the user.
//                    task.exception?.let { listener.onFailure(it) }
                }
            }
    }
}

