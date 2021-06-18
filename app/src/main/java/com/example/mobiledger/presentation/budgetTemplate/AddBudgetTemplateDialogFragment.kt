package com.example.mobiledger.presentation.budgetTemplate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.databinding.AddBudgetTemplateDialogFragmentBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.budgetTemplate.AddBudgetTemplateDialogFragmentViewModel.ViewErrorType

class AddBudgetTemplateDialogFragment :
    BaseDialogFragment<AddBudgetTemplateDialogFragmentBinding, BaseNavigator>
        (R.layout.add_budget_template_dialog_fragment) {

    private val viewModel: AddBudgetTemplateDialogFragmentViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setOnClickListeners()
    }

    private fun setUpObserver() {

        viewModel.loadingState.observe(viewLifecycleOwner, {
            if (it) {
                viewBinding.addTemplateProgressBar.visibility = View.VISIBLE
                isCancelable = false
            } else {
                viewBinding.addTemplateProgressBar.visibility = View.GONE
                isCancelable = true
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })

        viewModel.dataAdded.observe(viewLifecycleOwner, {
            if (it) {
                dismiss()
            }
        })
    }

    private fun setOnClickListeners() {
        viewBinding.apply {
            btnSubmitTemplate.setOnClickListener {
                val name = templateNameTv.text.toString()
                val maxLimitAmount = amountTv.text.toString().toLong()
                val description = descriptionTv.text.toString()
                viewModel.addNewBudgetTemplate(name, maxLimitAmount, description)
            }
        }
    }

    companion object {
        fun newInstance() = AddBudgetTemplateDialogFragment()
    }
}
