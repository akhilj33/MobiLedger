package com.example.mobiledger.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


sealed class NotificationEntity(val type: Type, val link: Link, val action: Action?) : Parcelable {
    lateinit var inAppMessage: String
    lateinit var backgroundNotificationMessage: String

//    @Parcelize
//    data class PaymentReceivedNotification(
//        val notificationType: Type,
//        val deepLinkType: Link,
//        val actionType: Action?,
//        val payload: PaymentReceivedNotificationPayload
//    ) : NotificationEntity(notificationType, deepLinkType, actionType)
//
    @Parcelize
    enum class Type(val type: String) : Parcelable {
        Warning("Warning"),
        Success("Success"),
        Info("Info");

        companion object {
            fun getType(type: String?): Type? {
                return when (type) {
                    Warning.type -> Warning
                    Success.type -> Success
                    Info.type -> Info
                    else -> null
                }
            }
        }
    }

    @Parcelize
    enum class Action(val action: String) : Parcelable {
        Refresh("Refresh");

        companion object {
            fun getAction(action: String?): Action? {
                return when (action) {
                    Refresh.action -> Refresh
                    else -> null
                }
            }
        }
    }

    @Parcelize
    enum class Link(val link: String) : Parcelable {
        Prediction("Prediction"),
        InvoiceDetails("InvoiceDetails"),
        BankLineDetails("LineDetails"),
        BankLineList("LinesList"),
        Reconciliation("Reconciliation");

        companion object {
            fun getLink(link: String?): Link? {
                return when (link) {
                    Prediction.link -> Prediction
                    InvoiceDetails.link -> InvoiceDetails
                    BankLineDetails.link -> BankLineDetails
                    BankLineList.link -> BankLineList
                    Reconciliation.link -> Reconciliation
                    else -> null
                }
            }
        }
    }
}

//// Payload for notifications
//@Parcelize
//data class PaymentReceivedNotificationPayload(val transactionID: String) : Parcelable
