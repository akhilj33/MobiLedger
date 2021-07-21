package com.example.mobiledger.presentation.budget.budgetscreen

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.common.utils.showAddBudgetDialogFragment
import com.example.mobiledger.common.utils.showApplyTemplateDialogFragment
import com.example.mobiledger.common.utils.showUpdateBudgetDialogFragment
import com.example.mobiledger.databinding.FragmentBudgetBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.ConditionalOneTimeObserver
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.budget.BudgetViewItem
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.main.NavTab

class BudgetFragment : BaseFragment<FragmentBudgetBinding, BudgetNavigator>(R.layout.fragment_budget) {

    private val viewModel: BudgetViewModel by viewModels { viewModelFactory }

    private val budgetAdapter: BudgetAdapter by lazy {
        BudgetAdapter(
            onSetMonthlyLimitBtnClick,
            onApplyTemplateClick,
            onUpdateMonthlyLimitClick,
            onAddBudgetCategoryClick,
            onUpdateBudgetCategoryClick
        )
    }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun swipeRefreshLayout(): SwipeRefreshLayout {
        return viewBinding.swipeRefreshLayout
    }

    override fun refreshView() {
        hideSnackBarErrorView()
        viewModel.reloadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setUpObserver()
        initRecyclerView()
        viewModel.getBudgetData()
    }

    private fun resetState() {
        viewBinding.apply {
            rvBudget.scrollToPosition(0)
        }
        viewModel.getBudgetData()
    }

    private fun setOnClickListener() {
        viewBinding.apply {
            monthNavigationBar.leftArrow.setOnClickListener { handleLeftClick() }
            monthNavigationBar.rightArrow.setOnClickListener { handleRightClick() }

            tvResetBudget.setOnClickListener {
                viewModel.resetBudget()
            }
        }
    }

    private fun setUpObserver() {

        activityViewModel.currentTab.observe(viewLifecycleOwner, ConditionalOneTimeObserver { tab ->
            return@ConditionalOneTimeObserver when (tab) {
                is NavTab.BUDGET -> {
                    resetState()
                    true
                }
                else -> false
            }
        })

        activityViewModel.updateBudgetResultLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            refreshView()
        })

        activityViewModel.addBudgetResultLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            refreshView()
        })

        activityViewModel.templateAppliedReload.observe(viewLifecycleOwner, OneTimeObserver {
            viewModel.getBudgetData()
        })

        viewModel.budgetViewItemListLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            budgetAdapter.addItemList(it)
            if (it.size==1 && it[0] is BudgetViewItem.BudgetEmpty)
                viewBinding.tvResetBudget.gone()
            else viewBinding.tvResetBudget.visible()
        })

        viewModel.monthNameLiveData.observe(viewLifecycleOwner, {
            viewBinding.monthNavigationBar.tvMonth.text = it
        })

        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                showSwipeRefresh()
            } else {
                hideSwipeRefresh()
            }
        })

        viewModel.resetBudgetLiveData.observe(viewLifecycleOwner, OneTimeObserver{
            refreshView()
        })
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvBudget.apply {
            layoutManager = linearLayoutManager
            adapter = budgetAdapter
        }
    }

    private val onSetMonthlyLimitBtnClick = fun() {
        showAddBudgetDialogFragment(
            requireActivity().supportFragmentManager,
            viewModel.monthlyLimit,
            viewModel.giveFinalExpenseList(),
            DateUtils.getDateInMMyyyyFormat(viewModel.getCurrentMonth()),
            0, false
        )
    }

    private val onApplyTemplateClick = fun() {
        showApplyTemplateDialogFragment(
            requireActivity().supportFragmentManager,
            DateUtils.getDateInMMyyyyFormat(viewModel.getCurrentMonth())
        )
    }

    private val onUpdateMonthlyLimitClick = fun() {
        showAddBudgetDialogFragment(
            requireActivity().supportFragmentManager,
            viewModel.monthlyLimit,
            viewModel.giveFinalExpenseList(),
            DateUtils.getDateInMMyyyyFormat(viewModel.getCurrentMonth()),
            viewModel.budgetTotal, false
        )
    }

    private val onUpdateBudgetCategoryClick = fun(category: String, categoryBudget: Long) {
        showUpdateBudgetDialogFragment(
            requireActivity().supportFragmentManager,
            viewModel.getCurrentMonth(),
            category,
            categoryBudget,
            MonthlyBudgetData(viewModel.monthlyLimit, viewModel.budgetTotal)
        )
    }

    private val onAddBudgetCategoryClick = fun() {
        showAddBudgetDialogFragment(
            requireActivity().supportFragmentManager,
            viewModel.monthlyLimit,
            viewModel.giveFinalExpenseList(),
            DateUtils.getDateInMMyyyyFormat(viewModel.getCurrentMonth()),
            viewModel.budgetTotal, true
        )
    }

    private fun handleRightClick() {
        if (!viewModel.isCurrentMonth()) {
            viewModel.getNextMonthData()
        }
        handleRightArrowState()
    }

    private fun handleLeftClick() {
        viewModel.getPreviousMonthData()
        handleRightArrowState()
    }

    private fun handleRightArrowState() {
        val color = if (!viewModel.isCurrentMonth())
            R.color.prussianBlue
        else
            R.color.colorGrey

        ImageViewCompat.setImageTintList(
            viewBinding.monthNavigationBar.rightArrow,
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
        )
    }

    companion object {
        fun newInstance() = BudgetFragment()
    }
}