package com.example.mobiledger.presentation.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class AuthViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val itemCount = 2

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment = LoginFragment()
        when (position) {
            0 -> fragment = LoginFragment()
            1 -> fragment = SignUpFragment()
        }
        return fragment
    }

    override fun getItemCount(): Int {
        return itemCount
    }
}