package com.example.foodstation.data.sources.api

object DefaultHeaderBuilder {
    fun getDefaultHeaders(
            contentType: ContentType,
            accessToken: String
    ): MutableMap<String, String> {
        val content = when (contentType) {
            ContentType.JSON -> ContentType.JSON.type
            ContentType.URL_ENCODED -> ContentType.URL_ENCODED.type
        }
        val map: MutableMap<String, String> = mutableMapOf()
        map["user-key"] = accessToken
        map["Accept"] = content
        return map
    }
}

enum class ContentType(val type: String) {
    URL_ENCODED("application/x-www-form-urlencoded"), JSON("application/json")
}