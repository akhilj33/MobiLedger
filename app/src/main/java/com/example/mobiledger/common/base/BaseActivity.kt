package com.example.mobiledger.common.base

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.mobiledger.R
import com.example.mobiledger.common.di.DependencyProvider
import com.example.mobiledger.presentation.main.MainActivity

abstract class BaseActivity<B : ViewDataBinding, out NV>(
    @LayoutRes
    private val layoutId: Int
) : AppCompatActivity() {

    lateinit var viewBinding: B

    protected val viewModelFactory = DependencyProvider.provideViewModelFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(
            this, layoutId
        )
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val newOverride = Configuration(newBase?.resources?.configuration)
        if (newOverride.fontScale > 1.0f) {
            newOverride.fontScale = 1.0f
            applyOverrideConfiguration(newOverride)
        }
    }

    abstract fun getFragmentNavigator(): NV?

    // -------------------- Notification --------------------
    protected fun sendNotification(notificationManager: NotificationManager?, CHANNEL_ID: String, textTitle: String, textContent: String) {

        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationID = (0..1000000).random()
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)

        notificationManager?.notify(notificationID, notificationBuilder.build())
    }

    protected fun createNotificationChannel(notificationManager: NotificationManager?, channelDescription: String, CHANNEL_ID: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_transaction)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = channelDescription
            }
            // Register the channel with the system
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
