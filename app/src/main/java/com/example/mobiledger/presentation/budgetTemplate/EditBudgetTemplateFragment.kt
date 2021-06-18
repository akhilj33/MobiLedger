package com.example.mobiledger.presentation.budgetTemplate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.databinding.FragmentEditBudgetTempleteBinding
import com.example.mobiledger.presentation.budgetTemplate.budgetTemplateAdapters.EditBudgetTemplateRecyclerViewAdapter


class EditBudgetTemplateFragment : BaseFragment<FragmentEditBudgetTempleteBinding, BaseNavigator>(R.layout.fragment_edit_budget_templete) {

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
                viewModel.addNewBudgetTemplateCategory("Anant", 400)
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
        viewModel.budgetTemplateCategoryList.observe(viewLifecycleOwner, {
            it.let {
                budgetTemplateCategoryRecyclerAdapter.addList(it.peekContent())
            }
        })

        viewModel.budgetTemplateSummary.observe(viewLifecycleOwner, {
            it.let {
                viewBinding.tvMaxBudgetAmt.text = it.peekContent().maxBudgetLimit.toString()
            }
        })

        viewModel.totalSum.observe(viewLifecycleOwner, {
            it.let {
                viewBinding.tvTotalBudgetAmt.text = it.toString()
            }
        })

        viewModel.dataAdded.observe(viewLifecycleOwner, {
            if (it) {
                viewModel.getBudgetTemplateCategoryList(viewModel.id)
            }
        })
    }

    private val onTemplateItemClick = fun(categoryName: String) {
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
