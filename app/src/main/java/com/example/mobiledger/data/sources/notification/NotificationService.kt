package com.example.mobiledger.data.sources.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class NotificationService : FirebaseMessagingService() {

    companion object {
        private var listener: NotificationListener? = null
        fun register(notificationListener: NotificationListener) {
            listener = notificationListener
            Timber.d("listener added")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("Firebase FCM Token $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.d("Notification message received ")
        if (message.data.isNotEmpty()) {
            listener?.onPushNotificationReceived(message)
        }
    }
}