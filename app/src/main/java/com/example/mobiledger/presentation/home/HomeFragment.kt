package com.example.mobiledger.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.utils.showRecordTransactionDialogFragment
import com.example.mobiledger.databinding.FragmentHomeBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeNavigator>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
    }

    private fun setOnClickListener() {
        viewBinding.homeToolbar.btnProfile.setOnClickListener {
            navigator?.navigateToProfileScreen()
        }

        viewBinding.btnAddTransaction.setOnClickListener {
            showRecordTransactionDialogFragment(requireActivity().supportFragmentManager)
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}