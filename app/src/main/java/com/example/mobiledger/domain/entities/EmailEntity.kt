package com.example.mobiledger.domain.entities

import android.net.Uri

data class EmailEntity(
    val email: String = "",
    val subject: String = "",
    val bodyText: String = "",
    val attachmentPath: Uri? = null
)
