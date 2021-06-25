package com.example.mobiledger.domain.entities

import androidx.annotation.StringRes

data class OnBoardingCarouselEntity(
    @StringRes val carouselTitle: Int,
    @StringRes val carouselSubTitle: Int,
    val position: Int
)