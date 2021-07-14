package com.example.mobiledger.common.utils

import android.content.Intent
import android.net.Uri
import com.example.mobiledger.domain.entities.EmailEntity

object EmailUtils {
    fun getIntent(
        emailEntity: EmailEntity
    ): Intent {
        val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
        emailSelectorIntent.data = Uri.parse("mailto:")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailEntity.email))
            putExtra(Intent.EXTRA_SUBJECT, emailEntity.subject)
            putExtra(Intent.EXTRA_TEXT, emailEntity.bodyText)
            selector = emailSelectorIntent

            if (emailEntity.attachmentPath != null) {
                putExtra(Intent.EXTRA_STREAM, emailEntity.attachmentPath)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        }
        return emailIntent
    }
}
