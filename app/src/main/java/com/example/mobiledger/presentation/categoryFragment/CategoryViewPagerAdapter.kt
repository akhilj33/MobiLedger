package com.example.mobiledger.presentation.categoryFragment

import IncomeCategoryFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class CategoryViewPagerAdapter(fm: FragmentManager?, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm!!, lifecycle) {

    private val int_items = 2

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = ExpenseCategoryFragment()
            1 -> fragment = IncomeCategoryFragment()
        }
        return fragment!!
    }

    override fun getItemCount(): Int {
        return int_items
    }
}