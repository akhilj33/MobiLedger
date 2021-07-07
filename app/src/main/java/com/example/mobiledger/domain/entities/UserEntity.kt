package com.example.mobiledger.domain.entities

import com.example.mobiledger.domain.enums.SignInType

data class UserEntity(
    val uid: String,
    val userName: String? = null,
    val photoUrl: String? = null,
    val emailId: String? = null,
    val phoneNo: String? = null,
    val signInType: SignInType
) {
    constructor() : this(uid = "", signInType = SignInType.Email)
}

