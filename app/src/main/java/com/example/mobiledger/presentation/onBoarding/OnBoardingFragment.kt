package com.example.mobiledger.presentation.onBoarding

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.extention.changeStatusBarColor
import com.example.mobiledger.databinding.FragmentOnboardingBinding
import com.example.mobiledger.domain.entities.OnBoardingCarouselEntity

class OnBoardingFragment : BaseFragment<FragmentOnboardingBinding, OnBoardingNavigator>(R.layout.fragment_onboarding) {

    private val onBoardingViewModel: OnBoardingViewModel by viewModels { viewModelFactory }

    private lateinit var carouselLayoutManager: LinearLayoutManager

    private lateinit var slidingImageDots: Array<ImageView?>
    private var slidingDotsCount = 0

    override fun isBottomNavVisible(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
        initCarouselData()
    }

    private fun setOnClickListener() {
        viewBinding.btnNext.setOnClickListener {
            navigator?.navigateToAuthScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        viewBinding.root.changeStatusBarColor(
            requireActivity(),
            StatusBarColor.TRANSPARENT,
            isFullScreen = true
        )
    }

    private fun initCarouselData() {
        val carouselDataList: List<OnBoardingCarouselEntity> =
            onBoardingViewModel.initCarouselData()
        initCarousel(carouselDataList)
    }

    private fun initCarousel(carouselEntityList: List<OnBoardingCarouselEntity>) {

        carouselLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()

        val carouselAdapter =
            OnBoardingCarouselAdapter(carouselEntityList)
        viewBinding.carouselRv.apply {
            layoutManager = carouselLayoutManager
            adapter = carouselAdapter
            snapHelper.attachToRecyclerView(this)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> handleIndicator(
                            snapHelper
                        )
                    }
                }
            })
        }
        slidingDotsCount = carouselEntityList.size
        initSlider()
    }

    private fun initSlider() {
        slidingImageDots = arrayOfNulls(slidingDotsCount)
        for (i in 0 until slidingDotsCount) {
            slidingImageDots[i] = ImageView(requireContext())
            slidingImageDots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.carousel_dot_inactive
                )
            )
            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(10, 0, 10, 0)
            viewBinding.dotIndicatorLayout.addView(slidingImageDots[i], params)
        }

        slidingImageDots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.carousel_dot_active
            )
        )
    }

    private fun handleIndicator(snapHelper: PagerSnapHelper) {
        val centerView = snapHelper.findSnapView(carouselLayoutManager)
        val pos = carouselLayoutManager.getPosition(centerView!!)

        for (i in 0 until slidingDotsCount) {
            slidingImageDots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.carousel_dot_inactive
                )
            )
        }

        slidingImageDots[pos]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.carousel_dot_active
            )
        )
    }

    companion object {
        fun newInstance() = OnBoardingFragment()
    }
}