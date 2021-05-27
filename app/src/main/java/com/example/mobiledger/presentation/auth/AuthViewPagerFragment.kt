package com.example.mobiledger.presentation.auth

import android.os.Bundle
import android.view.View
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.transformer.ZoomOutPageTransformer
import com.example.mobiledger.databinding.FragmentAuthBinding
import com.google.android.material.tabs.TabLayoutMediator

class AuthViewPagerFragment : BaseFragment<FragmentAuthBinding, LoginNavigator>(R.layout.fragment_auth) {

    override fun isBottomNavVisible(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    private fun initViewPager() {
        viewBinding.pager.isUserInputEnabled = true
        viewBinding.pager.setPageTransformer(ZoomOutPageTransformer())
        viewBinding.pager.adapter =
            AuthViewPagerAdapter(
                this.parentFragmentManager,
                lifecycle
            )
        TabLayoutMediator(
            viewBinding.tabLayout,
            viewBinding.pager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "LOGIN"
                1 -> tab.text = "SIGN UP"
            }
        }.attach()
    }

    companion object {
        fun newInstance() = AuthViewPagerFragment()
    }
}