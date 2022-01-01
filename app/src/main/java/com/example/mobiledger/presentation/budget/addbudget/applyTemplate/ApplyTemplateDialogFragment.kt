package com.example.mobiledger.presentation.budget.addbudget.applyTemplate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.showAlertDialog
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.databinding.ApplyTemplateDialogLayoutBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.budgetTemplate.BudgetTemplateNavigator


class ApplyTemplateDialogFragment :
    BaseDialogFragment<ApplyTemplateDialogLayoutBinding, BudgetTemplateNavigator>(R.layout.apply_template_dialog_layout) {

    private val viewModel: ApplyTemplateViewModel by viewModels { viewModelFactory }

    private val applyTemplateRecyclerViewAdapter: ApplyTemplateRecyclerViewAdapter by lazy {
        ApplyTemplateRecyclerViewAdapter(
            onTemplateItemClick
        )
    }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(KEY_MONTH)?.let {
                viewModel.month = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        initRecyclerView()
        viewModel.getBudgetTemplateList()
    }

    private fun setUpObserver() {
        viewModel.budgetTemplateList.observe(viewLifecycleOwner, {
            it.let {
                applyTemplateRecyclerViewAdapter.addList(it.peekContent())
                if (it.peekContent().isNotEmpty()) {
                    viewBinding.tvNoTemplate.gone()
                } else {
                    viewBinding.tvNoTemplate.visible()
                }
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, {
            it.let {
                if (it) {
                    viewBinding.rvTemplateListProgressBar.visible()
                    viewBinding.rvBudgetTemplateList.gone()
                } else {
                    viewBinding.rvTemplateListProgressBar.gone()
                    viewBinding.rvBudgetTemplateList.visible()
                }
            }
        })

        viewModel.templateApplied.observe(viewLifecycleOwner, OneTimeObserver {
            activityViewModel.templateApplied()
            dismiss()
        })

    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvBudgetTemplateList
            .apply {
                layoutManager = linearLayoutManager
                adapter = applyTemplateRecyclerViewAdapter
            }
    }

    private val onTemplateItemClick = fun(id: String) {
        viewModel.selectedId = id
        activity?.showAlertDialog(
            getString(R.string.apply_template),
            getString(R.string.apply_template_msg),
            getString(R.string.yes),
            getString(R.string.no),
            onCancelButtonClick,
            onContinueClick
        )
    }

    private val onCancelButtonClick = {}

    private val onContinueClick = {
        viewModel.applyTemplate(viewModel.selectedId)
    }

    companion object {
        private const val KEY_MONTH = "MONTH"
        fun newInstance(month: String) = ApplyTemplateDialogFragment()
            .apply {
                arguments = Bundle().apply {
                    putString(KEY_MONTH, month)
                }
            }
    }
}