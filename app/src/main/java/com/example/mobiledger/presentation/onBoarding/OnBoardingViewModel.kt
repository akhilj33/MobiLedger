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

    fun initCarouselData(): List<OnBoardingCarouselEntity> {
        val carouselList: ArrayList<OnBoardingCarouselEntity> = ArrayList()
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.apply_template_msg,
                R.string.confirm_password_mismatched_msg,
                constZero
            )
        )
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.apply_template_msg,
                R.string.confirm_password_mismatched_msg,
                constOne
            )
        )
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.apply_template_msg,
                R.string.confirm_password_mismatched_msg,
                constTwo
            )
        )
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.apply_template_msg,
                R.string.confirm_password_mismatched_msg,
                constThree
            )
        )
        carouselList.add(
            OnBoardingCarouselEntity(
                R.string.apply_template_msg,
                R.string.confirm_password_mismatched_msg,
                constFour
            )
        )
        return carouselList
    }
}