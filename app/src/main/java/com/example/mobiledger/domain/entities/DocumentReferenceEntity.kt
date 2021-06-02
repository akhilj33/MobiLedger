package com.example.mobiledger.domain.entities

import com.google.firebase.firestore.DocumentReference

data class DocumentReferenceEntity(
    val transRef: DocumentReference?=null
)
