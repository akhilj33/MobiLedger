package com.example.mobiledger.presentation.stats

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.databinding.FragmentStatsBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.ConditionalOneTimeObserver
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.main.NavTab

class StatsFragment : BaseFragment<FragmentStatsBinding, StatsNavigator>(R.layout.fragment_stats) {

    private val viewModel: StatsViewModel by viewModels { viewModelFactory }
    private val statsAdapter: StatsAdapter by lazy { StatsAdapter(onCategoryItemClick) }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun swipeRefreshLayout(): SwipeRefreshLayout {
        return viewBinding.swipeRefreshLayout
    }

    override fun refreshView() {
        hideSnackBarErrorView()
        viewModel.reloadData(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setUpObservers()
        setOnClickListener()
    }

    private fun resetState() {
        viewBinding.apply {
            rvStats.scrollToPosition(0)
        }
        viewModel.getStatsData()
    }

    private fun setUpObservers() {
        activityViewModel.currentTab.observe(viewLifecycleOwner, ConditionalOneTimeObserver { tab ->
            when (tab) {
                is NavTab.STATS -> {
                    resetState()
                    true
                }
                else -> false
            }
        })

        viewModel.statsViewItemListLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            statsAdapter.addItemList(it)
        })

        viewModel.monthNameLiveData.observe(viewLifecycleOwner, {
            viewBinding.monthNavigationBar.tvMonth.text = it
        })

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showSwipeRefresh()
            } else {
                hideSwipeRefresh()
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            showSnackBarErrorView(it.message ?: getString(it.resID), false)
        })

        viewModel.authErrorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
//            navigateToLoginScreen() todo
        })
    }

    private fun setOnClickListener() {
        viewBinding.apply {
            monthNavigationBar.leftArrow.setOnClickListener { handleLeftClick() }
            monthNavigationBar.rightArrow.setOnClickListener { handleRightClick() }
        }
        viewBinding.ivProfileIcon.setOnClickListener {
            navigator?.navigateToProfileScreen()
        }
        viewBinding.horizontalGuideline2
    }

    private fun handleRightClick() {
        if (!viewModel.isCurrentMonth()) {
            viewModel.getNextMonthData()
        }
        handleRightArrowState()
    }

    private fun handleLeftClick() {
        viewModel.getPreviousMonthData()
        handleRightArrowState()
    }

    private fun handleRightArrowState() {
        val color = if (!viewModel.isCurrentMonth())
            R.color.prussianBlue
        else
            R.color.colorGrey

        ImageViewCompat.setImageTintList(
            viewBinding.monthNavigationBar.rightArrow,
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
        )
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvStats.apply {
            layoutManager = linearLayoutManager
            adapter = statsAdapter
        }
    }

    private val onCategoryItemClick = fun(categoryNameList: List<String>, amount: Long) {
        navigator?.navigateToStatsDetailScreen(categoryNameList, amount, viewModel.getCurrentMonth())
    }

    companion object {
        fun newInstance() = StatsFragment()
    }
}