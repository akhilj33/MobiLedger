package com.example.mobiledger.common.listener

import java.lang.Exception

interface AuthListener<T> {

    fun onResponse(response: T)

    fun onFailure(exception: Exception)

}