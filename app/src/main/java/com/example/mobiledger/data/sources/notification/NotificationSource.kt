package com.example.mobiledger.data.sources.notification

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.common.utils.JsonUtils
import com.example.mobiledger.common.utils.NotificationUtils
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.NotificationEntity
import com.example.mobiledger.presentation.Event
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface NotificationSource {
    fun emitNotification(): Flow<Event<NotificationEntity>>
    fun initializeNotificationSDK()
    suspend fun getFcmToken(): AppResult<String>
}

interface NotificationListener {
    fun onPushNotificationReceived(message: RemoteMessage)
}

class NotificationSourceImpl(private val context: Context, private val scope: CoroutineScope = GlobalScope) : NotificationSource,
    NotificationListener {

    private val _notificationEvents = MutableSharedFlow<Event<NotificationEntity>>(1, 0, BufferOverflow.DROP_OLDEST)
    private val notificationEvents: SharedFlow<Event<NotificationEntity>> = _notificationEvents.asSharedFlow()

    private fun createNotificationChannel() {
        NotificationUtils.createNotificationChannel(context)
    }

    private fun initializeNotificationListener() {
        NotificationService.register(this)
    }

    override fun initializeNotificationSDK() {
        createNotificationChannel()
        initializeNotificationListener()
    }

    private fun publishInAppNotification(it: Event<NotificationEntity>) {
        scope.launch { _notificationEvents.emit(it) }
    }

    @FlowPreview
    override fun emitNotification(): Flow<Event<NotificationEntity>> = notificationEvents

    override suspend fun getFcmToken(): AppResult<String> {
        return suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token: String? = task.result
                    if (token.isNullOrEmpty() || token.isBlank()) {
                        continuation.resume(AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR)))
                    } else {
                        continuation.resume(AppResult.Success(token))
                    }
                } else {
                    continuation.resume(AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR)))
                }
            }
        }
    }

    //-----------------------Notification Listener-------------------------------------------------
    override fun onPushNotificationReceived(message: RemoteMessage) {
        val notificationEntity: NotificationEntity = mapToEntity(message) ?: return

        //check foreground and background
        if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            publishInAppNotification(Event(notificationEntity))
        } else {
//            NotificationUtils.showNativeNotification(context, notificationEntity)
        }
    }

//-------------------------------Mapper-------------------------------------------------------------

    private fun mapToEntity(message: RemoteMessage): NotificationEntity? {
        val type: NotificationEntity.Type = NotificationEntity.Type.getType(message.data["type"]) ?: return null
        val inAppMessage: String = message.data["body"] ?: return null
        val nativeNotificationMessage: String = message.data["message"] ?: return null
        val deepLinkResponse: DeepLinkResponse =
            JsonUtils.convertJsonStringToObject<DeepLinkResponse>(message.data["deeplink"]) ?: return null

        val linkType: NotificationEntity.Link = NotificationEntity.Link.getLink(deepLinkResponse.link) ?: return null
        val actionType: NotificationEntity.Action? = NotificationEntity.Action.getAction(deepLinkResponse.action)

        val payloadJson = message.data["payload"]

//        val notificationEntity = when (linkType) {
//            //getPayload by mapping
//        }

        return null
//        return notificationEntity.apply {
//            this.inAppMessage = inAppMessage
//            this.backgroundNotificationMessage = nativeNotificationMessage
//        }
    }
}