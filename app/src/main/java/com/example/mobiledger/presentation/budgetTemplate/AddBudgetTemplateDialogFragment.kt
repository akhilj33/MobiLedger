package com.example.mobiledger.presentation.budgetTemplate

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.utils.JsonUtils
import com.example.mobiledger.databinding.AddBudgetTemplateDialogFragmentBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.domain.entities.NewBudgetTemplateEntity
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.budgetTemplate.AddBudgetTemplateViewModel.ViewErrorType

class AddBudgetTemplateDialogFragment :
    BaseDialogFragment<AddBudgetTemplateDialogFragmentBinding, BaseNavigator>
        (R.layout.add_budget_template_dialog_fragment) {

    private val viewModel: AddBudgetTemplateViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(TEMPLATE_LIST)?.let {
                viewModel.templateList = JsonUtils.convertJsonStringToObject(it) ?: mutableListOf()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setOnClickListeners()
    }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

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
                activityViewModel.addNewBudgetTemplate()
                dismiss()
            }
        })
    }

    private fun setOnClickListeners() {
        viewBinding.apply {
            btnSubmitTemplate.setOnClickListener {
                if (doValidations()){
                    viewModel.addNewBudgetTemplate(getName(), getAmount().toLong(), getDescription())
                }
            }

            amountTv.addTextChangedListener(amountTextWatcher)
            templateNameTv.addTextChangedListener(nameTextWatcher)
        }
    }

    /*---------------------------------------Validations------------------------------------------*/

    private fun doValidations(): Boolean {
        updateNameViewBasedOnValidation(isValidName())
        updateAmountViewBasedOnValidation(isValidAmount())

        return isValidName() && isValidAmount() && !isTemplateNameRepeating()
    }

    private fun getName(): String = viewBinding.templateNameTv.text.toString().trim()
    private fun getAmount(): String = viewBinding.amountTv.text.toString().trim()
    private fun getDescription(): String = viewBinding.descriptionTv.text.toString().trim()

    private fun isValidName(): Boolean = getName().isNotBlank()
    private fun isValidAmount(): Boolean = getAmount().isNotBlank() && getAmount().toLong() > 0L
    private fun isTemplateNameRepeating(): Boolean = viewModel.templateList.find { it.name.trim().lowercase() == getName().trim().lowercase() } != null

    private val nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidName()) {
                updateNameViewBasedOnValidation(isValid = true)
            }
        }
    }

    private val amountTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidAmount()) {
                updateAmountViewBasedOnValidation(isValid = true)
            }
        }
    }

    private fun updateNameViewBasedOnValidation(isValid: Boolean) {
        if (isValid) {
            if (isTemplateNameRepeating())
                 viewBinding.templateNameLayout.error = getString(R.string.template_already_exist)
            else viewBinding.templateNameLayout.error = null
        } else {
            viewBinding.templateNameLayout.error = getString(R.string.field_invalid)
        }
    }

    private fun updateAmountViewBasedOnValidation(isValid: Boolean) {
        if (isValid) {
            viewBinding.maxLimitLayout.error = null
        } else {
            viewBinding.maxLimitLayout.error = getString(R.string.amount_invalid)
        }
    }

    companion object {
        private const val TEMPLATE_LIST = "TEMPLATE_LIST"
        fun newInstance(templateList: MutableList<NewBudgetTemplateEntity>) =
            AddBudgetTemplateDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(TEMPLATE_LIST, JsonUtils.convertToJsonString(templateList))
                }
            }
    }
}
