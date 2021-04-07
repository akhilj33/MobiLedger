package com.example.mobiledger.presentation.dashboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class DashboardAdapter(
    fragment: Fragment,
    private val totalCount: Int,
    private val getFragmentInstance: (Int) -> Fragment
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = totalCount

    override fun createFragment(position: Int): Fragment {
        return getFragmentInstance(position)
    }
}