package com.example.mobiledger.presentation.budgetTemplate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.utils.showAddNewTemplateDialogFragment
import com.example.mobiledger.databinding.FragmentBudgetTemplateBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.budgetTemplate.budgetTemplateAdapters.BudgetTemplateFragmentRecyclerAdapter

class BudgetTemplateFragment : BaseFragment<FragmentBudgetTemplateBinding, BudgetTemplateNavigator>(R.layout.fragment_budget_template) {

    private val viewModel: BudgetTemplateViewModel by viewModels { viewModelFactory }

    override fun isBottomNavVisible(): Boolean = false

    private val budgetTemplateFragmentRecyclerAdapter: BudgetTemplateFragmentRecyclerAdapter by lazy {
        BudgetTemplateFragmentRecyclerAdapter(
            onTemplateItemClick
        )
    }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setUpObserver()
        initRecyclerView()
        viewModel.refreshData()
    }

    private fun setOnClickListener() {
        viewBinding.apply {
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }
            btnNewTemplate.setOnClickListener {
                showAddNewTemplateDialogFragment(requireActivity().supportFragmentManager)
            }
        }
    }

    private fun setUpObserver() {
        viewModel.budgetTemplateList.observe(viewLifecycleOwner, {
            it.let {
                budgetTemplateFragmentRecyclerAdapter.addList(it.peekContent())
            }
        })
        activityViewModel.addNewBudgetTemplate.observe(viewLifecycleOwner, OneTimeObserver {
            it.let {
                viewModel.refreshData()
            }
        })
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvBudgetTemplates
            .apply {
                layoutManager = linearLayoutManager
                adapter = budgetTemplateFragmentRecyclerAdapter
            }
    }

    private val onTemplateItemClick = fun(id: String) {
        navigator?.navigateToEditBudgetTemplateScreen(id)
    }

    companion object {
        fun newInstance() = BudgetTemplateFragment()
    }
}