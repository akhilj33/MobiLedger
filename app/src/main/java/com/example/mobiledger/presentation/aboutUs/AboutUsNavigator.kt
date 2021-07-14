package com.example.mobiledger.presentation.aboutUs

import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.domain.entities.EmailEntity

interface AboutUsNavigator : BaseNavigator {
    fun sendEmail(emailEntity: EmailEntity)
}