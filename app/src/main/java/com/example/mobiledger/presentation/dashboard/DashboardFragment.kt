package com.example.mobiledger.presentation.dashboard

import android.os.Bundle
import android.view.View
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.databinding.FragmentDashboardBinding
import com.example.mobiledger.presentation.NormalObserver
import com.example.mobiledger.presentation.SplitFragment
import com.example.mobiledger.presentation.budget.budgetscreen.BudgetFragment
import com.example.mobiledger.presentation.home.HomeFragment
import com.example.mobiledger.presentation.stats.StatsFragment
import com.example.mobiledger.presentation.main.MainActivityViewModel.*
import com.example.mobiledger.presentation.main.NavTab

class DashboardFragment :
    BaseFragment<FragmentDashboardBinding, DashboardNavigator>(R.layout.fragment_dashboard) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityViewModel.showBottomNavigationView()
        initializeUI()
        setUpObservers()
    }

    private fun initializeUI() {
        viewBinding.viewPager.apply {
            adapter = DashboardAdapter(this@DashboardFragment, 4) { position ->
                when (position) {
                    0 -> HomeFragment.newInstance()
                    1 -> BudgetFragment.newInstance()
                    2 -> StatsFragment.newInstance()
                    3 -> SplitFragment.newInstance()
                    else -> HomeFragment.newInstance()
                }
            }
            isUserInputEnabled = false
        }
    }

    private fun setUpObservers() {
        activityViewModel.currentTab.observe(viewLifecycleOwner, NormalObserver { selectedTab ->
            when (selectedTab) {
                is NavTab.HOME -> viewBinding.viewPager.setCurrentItem(
                    TabOrder.Home.ordinal,
                    false
                )
                is NavTab.BUDGET -> viewBinding.viewPager.setCurrentItem(
                    TabOrder.Budget.ordinal,
                    false
                )
                is NavTab.STATS ->
                    viewBinding.viewPager.setCurrentItem(
                        TabOrder.Insight.ordinal,
                        false
                    )
                is NavTab.SPLIT -> {
                    viewBinding.viewPager.setCurrentItem(
                        TabOrder.Split.ordinal,
                        false
                    )
                }
                is NavTab.DeselectAll -> {
                }
            }
        })
    }

    companion object {
        fun newInstance() = DashboardFragment()
    }

    private enum class TabOrder {
        Home, Budget, Insight, Split
    }
}