package com.example.mobiledger.common.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.mobiledger.R
import com.example.mobiledger.common.utils.ConstantUtils.CHANNEL_ID_TRANSACTION

object NotificationUtils {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_TRANSACTION, context.getString(R.string.channel_transaction),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = ""
            channel.setShowBadge(true)
            val notificationManager: NotificationManager? = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            //can also check for vibration and lock screen visibility
            notificationManager?.createNotificationChannel(channel)
        }
    }

//    fun showNativeNotification(context: Context, notificationEntity: NotificationEntity) {
//        val intent = MainActivity.getIntent(context, notificationEntity)
//
//        val builder = NotificationCompat.Builder(context, context.getString(R.string.default_notification_channel_id))
//            .setSmallIcon(R.drawable.notification_icon).setContentText(notificationEntity.backgroundNotificationMessage)
//            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationEntity.backgroundNotificationMessage))
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(PendingIntent.getActivity(context, Random().nextInt(), intent, PendingIntent.FLAG_ONE_SHOT))
//            .setAutoCancel(true)
//
//        //show notification
//        with(NotificationManagerCompat.from(context)) {
//            // for now using time as unique id
//            // since we are not doing anything based on notification id as of now
//            notify(System.currentTimeMillis().toInt(), builder.build())
//        }
//    }
}