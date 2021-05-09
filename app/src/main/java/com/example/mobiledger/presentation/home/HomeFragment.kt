package com.example.mobiledger.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.utils.showRecordTransactionDialogFragment
import com.example.mobiledger.databinding.FragmentHomeBinding

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeNavigator>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    private val homeAdapter: HomeAdapter by lazy {
        HomeAdapter().apply {
            addItemList(viewModel.homeItemList())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setOnClickListener()
        viewBinding.monthNavigationBar.tvMonth.text = "January 2019"
    }

    private fun setOnClickListener() {
        viewBinding.btnAddTransaction.setOnClickListener {
            showRecordTransactionDialogFragment(requireActivity().supportFragmentManager)
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvHome.apply {
            layoutManager = linearLayoutManager
            adapter = homeAdapter
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}