package com.example.mobiledger.presentation.onBoarding

import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.entities.OnBoardingCarouselEntity

class OnBoardingViewModel : BaseViewModel() {

    private val constZero = 0
    private val constOne = 1
    private val constTwo = 2
    private val constThree = 3
    private val constFour = 4
    private val constFive = 5

    fun initCarouselData(): List<OnBoardingCarouselEntity> {
        val carouselList: ArrayList<OnBoardingCarouselEntity> = ArrayList()
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.home_carousel_title,
                R.string.home_carousel_sub_title,
                constZero
            )
        )
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.budget_carousel_title,
                R.string.budget_carousel_sub_title,
                constOne
            )
        )
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.stats_carousel_title,
                R.string.stats_carousel_sub_title,
                constTwo
            )
        )
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.profile_carousel_title,
                R.string.profile_carousel_sub_title,
                constThree
            )
        )
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.budget_templates_carousel_title,
                R.string.budget_templates_carousel_sub_title,
                constFour
            )
        )
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.category_carousel_title,
                R.string.category_carousel_sub_title,
                constFive
            )
        )
        return carouselList
    }
}