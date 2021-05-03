package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.api.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitProvider {

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val gsonConverterFactory: GsonConverterFactory =
        GsonConverterFactory.create(
            gson
        )

    private val loggingInterceptor =
        HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .baseUrl("http://localhost/")
            .build()

    fun provideService(): ApiService = retrofit.create(ApiService::class.java)
}