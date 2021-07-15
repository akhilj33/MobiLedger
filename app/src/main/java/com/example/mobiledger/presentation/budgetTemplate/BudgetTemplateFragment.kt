package com.example.mobiledger.presentation.budgetTemplate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.extention.showAlertDialog
import com.example.mobiledger.common.utils.showAddNewTemplateDialogFragment
import com.example.mobiledger.databinding.FragmentBudgetTemplateBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.budgetTemplate.budgetTemplateAdapters.BudgetTemplateFragmentRecyclerAdapter

class BudgetTemplateFragment : BaseFragment<FragmentBudgetTemplateBinding, BudgetTemplateNavigator>(R.layout.fragment_budget_template, StatusBarColor.BLUE) {

    private val viewModel: BudgetTemplateViewModel by viewModels { viewModelFactory }

    override fun isBottomNavVisible(): Boolean = false

    private val budgetTemplateFragmentRecyclerAdapter: BudgetTemplateFragmentRecyclerAdapter by lazy {
        BudgetTemplateFragmentRecyclerAdapter(
            onTemplateItemClick, onDeleteItemClick
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
            btnNewTemplateEmpty.setOnClickListener {
                showAddNewTemplateDialogFragment(requireActivity().supportFragmentManager, viewModel.templateList)
            }

            btnNewTemplate.setOnClickListener {
                showAddNewTemplateDialogFragment(requireActivity().supportFragmentManager, viewModel.templateList)
            }
        }
    }

    private fun setUpObserver() {
        viewModel.budgetTemplateList.observe(viewLifecycleOwner, OneTimeObserver{
            it.let {
                budgetTemplateFragmentRecyclerAdapter.addList(it)
                if (it.isNotEmpty()) {
                    viewBinding.emptyScreenGroup.gone()
                    viewBinding.nonEmptyScreenGroup.visible()
                } else {
                    viewBinding.emptyScreenGroup.visible()
                    viewBinding.nonEmptyScreenGroup.gone()
                }
            }
        })

        activityViewModel.addNewBudgetTemplate.observe(viewLifecycleOwner, OneTimeObserver {
            it.let {
                viewModel.refreshData()
                logEvent(getString(R.string.budget_template_created_msg))
            }
        })

        viewModel.dataDeleted.observe(viewLifecycleOwner, OneTimeObserver{
            it.let {
                if (it) {
                    viewModel.refreshData()
                }
            }
        })
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvBudgetTemplates.apply {
                layoutManager = linearLayoutManager
                adapter = budgetTemplateFragmentRecyclerAdapter
            }
    }

    private val onTemplateItemClick = fun(id: String) {
        navigator?.navigateToEditBudgetTemplateScreen(id)
    }

    private val onDeleteItemClick = fun(id: String) {
        viewModel.id = id
        activity?.showAlertDialog(
                    getString(R.string.delete_budget_templates),
                    getString(R.string.delete_budget_templates_msg),
                    getString(R.string.yes),
                    getString(R.string.no),
                    onCancelButtonClick,
                    onContinueClick
                )
    }

    private val onCancelButtonClick = {
    }

    private val onContinueClick = {
        viewModel.deleteBudgetTemplate()
    }

    companion object {
        fun newInstance() = BudgetTemplateFragment()
    }
}