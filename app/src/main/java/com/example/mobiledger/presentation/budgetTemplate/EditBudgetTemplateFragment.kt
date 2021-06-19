package com.example.mobiledger.presentation.budgetTemplate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.showAlertDialog
import com.example.mobiledger.common.utils.showEditBudgetTemplateDialogFragment
import com.example.mobiledger.databinding.FragmentEditBudgetTempleteBinding
import com.example.mobiledger.domain.entities.BudgetTemplateCategoryEntity
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.budgetTemplate.budgetTemplateAdapters.EditBudgetTemplateRecyclerViewAdapter


class EditBudgetTemplateFragment :
    BaseFragment<FragmentEditBudgetTempleteBinding, BudgetTemplateNavigator>(R.layout.fragment_edit_budget_templete) {

    private val viewModel: EditBudgetTemplateViewModel by viewModels { viewModelFactory }

    override fun isBottomNavVisible(): Boolean = false

    private val budgetTemplateCategoryRecyclerAdapter: EditBudgetTemplateRecyclerViewAdapter by lazy {
        EditBudgetTemplateRecyclerViewAdapter(
            onTemplateItemClick
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(ID)?.let {
                viewModel.id = it
            }
        }
        viewModel.getBudgetTemplateCategoryList(viewModel.id)
        viewModel.getBudgetTemplateSummary(viewModel.id)
        viewModel.getExpenseCategoryList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setOnClickListener()
        initRecyclerView()
    }

    private fun setOnClickListener() {
        viewBinding.apply {
            btnAddCategoryBudget.setOnClickListener {
                showEditBudgetTemplateDialogFragment(
                    requireActivity().supportFragmentManager,
                    viewModel.id,
                    viewModel.giveFinalExpenseList(),
                    "",
                    0L,
                    viewModel.totalSumVal,
                    viewModel.maxLimit,
                    isAddCategory = true,
                    isUpdateMaxLimit = false
                )
            }
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }

            btnDelete.setOnClickListener {
                activity?.showAlertDialog(
                    getString(R.string.delete_budget_templates),
                    getString(R.string.delete_budget_templates_msg),
                    getString(R.string.yes),
                    getString(R.string.no),
                    onCancelButtonClick,
                    onContinueClick
                )
            }

            budgetCardLayout.setOnClickListener {
                showEditBudgetTemplateDialogFragment(
                    requireActivity().supportFragmentManager,
                    viewModel.id,
                    viewModel.giveFinalExpenseList(),
                    "",
                    0L,
                    viewModel.totalSumVal,
                    viewModel.maxLimit,
                    isAddCategory = false,
                    isUpdateMaxLimit = true
                )
            }
        }
    }

    private val onCancelButtonClick = {

    }

    private val onContinueClick = {
        viewModel.deleteBudgetTemplate()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvBudgetTemplatesCategory
            .apply {
                layoutManager = linearLayoutManager
                adapter = budgetTemplateCategoryRecyclerAdapter
            }
    }


    private fun setUpObserver() {
        viewModel.budgetTemplateCategoryList.observe(viewLifecycleOwner, {
            it.let {
                budgetTemplateCategoryRecyclerAdapter.addList(it.peekContent())
            }
        })

        viewModel.budgetTemplateSummary.observe(viewLifecycleOwner, {
            it.let {
                viewBinding.tvMaxBudgetAmt.text = it.peekContent().maxBudgetLimit.toString()
                if (!it.peekContent().description.isNullOrEmpty())
                    viewBinding.templateDetail.text = it.peekContent().description
            }
        })

        viewModel.totalSum.observe(viewLifecycleOwner, {
            it.let {
                viewBinding.tvTotalBudgetAmt.text = it.toString()
            }
        })

        viewModel.dataDeleted.observe(viewLifecycleOwner, {
            it.let {
                if (it)
                    navigator?.navigateToBudgetTemplateFragment()
            }
        })

        activityViewModel.updateBudgetTemplateScreen.observe(viewLifecycleOwner, OneTimeObserver {
            it.let {
                viewModel.refreshData()
            }
        })

    }

    private val onTemplateItemClick = fun(category: BudgetTemplateCategoryEntity) {
        showEditBudgetTemplateDialogFragment(
            requireActivity().supportFragmentManager,
            viewModel.id,
            viewModel.giveFinalExpenseList(),
            category.category,
            category.categoryBudget,
            viewModel.totalSumVal,
            viewModel.maxLimit,
            isAddCategory = false,
            isUpdateMaxLimit = false
        )
    }

    companion object {
        private const val ID = "ID"
        fun newInstance(id: String) = EditBudgetTemplateFragment()
            .apply {
                arguments = Bundle().apply {
                    putString(ID, id)
                }
            }
    }
}
