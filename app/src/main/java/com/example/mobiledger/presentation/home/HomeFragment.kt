package com.example.mobiledger.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.databinding.FragmentHomeBinding

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeNavigator>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}