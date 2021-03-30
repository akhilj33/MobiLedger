package com.example.mobiledger.domain.entities

import android.net.Uri

data class AuthEntity(
    val uId: String,
    val userName: String? = null,
    val photoUrl: Uri? = null,
    val emailId: String? = null,
    val phoneNo: String? = null
)
