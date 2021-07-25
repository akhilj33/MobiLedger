package com.example.mobiledger.presentation.categoryFragment

import android.os.Bundle
import android.view.View
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.common.transformer.ZoomOutPageTransformer
import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.databinding.FragmentCategoryBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.google.android.material.tabs.TabLayoutMediator

class CategoryFragment : BaseFragment<FragmentCategoryBinding, BaseNavigator>(R.layout.fragment_category, StatusBarColor.BLUE) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        setOnClickListener()
    }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun isBottomNavVisible(): Boolean = false

    private fun setOnClickListener() {
        viewBinding.btnBack.setOnSafeClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initViewPager() {
        viewBinding.pager.isUserInputEnabled = true
        viewBinding.pager.setPageTransformer(ZoomOutPageTransformer())
        viewBinding.pager.adapter =
            CategoryViewPagerAdapter(
                this.parentFragmentManager,
                lifecycle
            )
        TabLayoutMediator(
            viewBinding.tabLayout,
            viewBinding.pager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = ConstantUtils.EXPENSE
                1 -> tab.text = ConstantUtils.INCOME
            }
        }.attach()
    }

    companion object {
        fun newInstance() = CategoryFragment()
    }
}