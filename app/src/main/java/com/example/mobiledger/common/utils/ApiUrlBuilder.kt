package com.example.mobiledger.common.utils

object ApiUrlBuilder {

    //TODO : Add BaseConfig as in MobiLedger
    //private var MobiLedger_BASE_URL = BuildConfig.BASE_URL
    private var ZOM_BASE_URL = "https://developers.zomato.com/api/v2.1/cities"

    private const val API_KEY = "6b85288a453867e7cece71fc40660b15"


    fun getApiKey() = API_KEY

    fun getBaseUrl() = ZOM_BASE_URL
}