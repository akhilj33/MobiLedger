package com.example.mobiledger.presentation.budget

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.databinding.FragmentBudgetBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding


class BudgetFragment : BaseFragment<FragmentBudgetBinding, BudgetNavigator>(R.layout.fragment_budget) {

//    private val viewModel: BudgetViewModel by viewModels { viewModelFactory }

    private val budgetAdapter: BudgetAdapter by lazy { BudgetAdapter() }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun swipeRefreshLayout(): SwipeRefreshLayout {
        return viewBinding.swipeRefreshLayout
    }

    override fun refreshView() {
        hideSnackBarErrorView()
//        viewModel.reloadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        viewBinding.apply {
            ivProfileIcon.setOnClickListener {
                navigator?.navigateToProfileScreen()
            }

        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvBudget.apply {
            layoutManager = linearLayoutManager
            adapter = budgetAdapter
        }
    }

//    private fun handleRightClick() {
//        if (!viewModel.isCurrentMonth()) {
//            viewModel.getNextMonthData()
//        }
//        handleRightArrowState()
//    }
//
//    private fun handleLeftClick() {
//        viewModel.getPreviousMonthData()
//        handleRightArrowState()
//    }
//
//    private fun handleRightArrowState() {
//        val color = if (!viewModel.isCurrentMonth())
//            R.color.prussianBlue
//        else
//            R.color.colorGrey
//
//        ImageViewCompat.setImageTintList(
//            viewBinding.monthNavigationBar.rightArrow,
//            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
//        )
//    }

    companion object {
        fun newInstance() = BudgetFragment()
    }
}