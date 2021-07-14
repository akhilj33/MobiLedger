package com.example.mobiledger.data.sources.notification

import com.google.gson.annotations.SerializedName

data class DeepLinkResponse(
    @SerializedName("action")
    val action: String?,
    @SerializedName("link")
    val link: String?
)

