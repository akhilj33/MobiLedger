package com.example.mobiledger.common.di.providers

import android.content.Context
import com.example.mobiledger.data.sources.attachment.AttachmentSource
import com.example.mobiledger.data.sources.attachment.AttachmentSourceImpl

class AttachmentSourceProvider(
    authSourceProvider: AuthSourceProvider,
    firebaseProvider: FirebaseProvider,
    context: Context
) {

    private val attachmentSource: AttachmentSource =
        AttachmentSourceImpl(authSourceProvider.provideAuthSource(), firebaseProvider.provideFirebaseStorageReference(), context)

    fun provideAttachmentSource(): AttachmentSource = attachmentSource
}