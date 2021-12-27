package com.example.mobiledger.presentation.budgetTemplate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.extention.*
import com.example.mobiledger.common.utils.showEditBudgetTemplateDialogFragment
import com.example.mobiledger.databinding.FragmentEditBudgetTempleteBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.domain.entities.BudgetTemplateCategoryEntity
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.budgetTemplate.budgetTemplateAdapters.EditBudgetTemplateRecyclerViewAdapter


class EditBudgetTemplateFragment :
    BaseFragment<FragmentEditBudgetTempleteBinding, BudgetTemplateNavigator>(R.layout.fragment_edit_budget_templete, StatusBarColor.BLUE) {

    private val viewModel: EditBudgetTemplateViewModel by viewModels { viewModelFactory }

    override fun isBottomNavVisible(): Boolean = false

    private val budgetTemplateCategoryRecyclerAdapter: EditBudgetTemplateRecyclerViewAdapter by lazy {
        EditBudgetTemplateRecyclerViewAdapter(
            onTemplateItemClick
        )
    }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(KEY_ID)?.let {
                viewModel.id = it
            }
        }
        viewModel.getBudgetTemplateSummary(viewModel.id)
        viewModel.getBudgetTemplateCategoryList(viewModel.id)
        viewModel.getLeftOverBudgetCategoryList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setOnClickListener()
        initRecyclerView()
    }

    private fun setOnClickListener() {
        viewBinding.apply {

            btnAddCategoryBudget.setOnSafeClickListener {
                showEditBudgetTemplateDialogFragment(
                    requireActivity().supportFragmentManager,
                    viewModel.id,
                    viewModel.giveFinalExpenseList(),
                    "",
                    0L,
                    viewModel.budgetTemplateAmount.value?.peekContent()?:0L,
                    viewModel.maxLimit,
                    isAddCategory = true,
                    isUpdateMaxLimit = false
                )
            }
            btnBack.setOnSafeClickListener {
                activity?.onBackPressed()
            }

            tvEditBudgetMonthlyLimit.setOnSafeClickListener {
                showEditBudgetTemplateDialogFragment(
                    requireActivity().supportFragmentManager,
                    viewModel.id,
                    viewModel.giveFinalExpenseList(),
                    "",
                    0L,
                    viewModel.budgetTemplateAmount.value?.peekContent()?:0L,
                    viewModel.maxLimit,
                    isAddCategory = false,
                    isUpdateMaxLimit = true
                )
            }
        }
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
        viewModel.budgetTemplateCategoryList.observe(viewLifecycleOwner, OneTimeObserver{
            it.let {
                budgetTemplateCategoryRecyclerAdapter.addList(it)
                if (it.isNotEmpty()) {
                    viewBinding.tvNoTemplate.gone()
                } else {
                    viewBinding.tvNoTemplate.visible()
                }
            }
        })

        viewModel.budgetTemplateAmount.observe(viewLifecycleOwner, OneTimeObserver{
            it.let {
                val totalBudget = it
                val monthlyLimit = viewModel.maxLimit
                val percent = ((totalBudget.toFloat() / monthlyLimit.toFloat())*100).roundToOneDecimal().toPercent().trim()
                viewBinding.tvBudgetAmount.text = getString(R.string.total_budget_set, totalBudget.toString(), monthlyLimit.toString(), percent)
            }
        })

        activityViewModel.updateBudgetTemplateScreen.observe(viewLifecycleOwner, OneTimeObserver {
            it.let {
                it?.let { viewModel.maxLimit = it }
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
            viewModel.budgetTemplateAmount.value?.peekContent()?:0L,
            viewModel.maxLimit,
            isAddCategory = false,
            isUpdateMaxLimit = false
        )
    }

    companion object {
        private const val KEY_ID = "ID"
        fun newInstance(id: String) = EditBudgetTemplateFragment()
            .apply {
                arguments = Bundle().apply {
                    putString(KEY_ID, id)
                }
            }
    }
}
