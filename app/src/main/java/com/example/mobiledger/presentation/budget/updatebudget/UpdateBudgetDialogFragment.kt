package com.example.mobiledger.presentation.budget.updatebudget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.disable
import com.example.mobiledger.common.extention.enable
import com.example.mobiledger.common.utils.DateUtils.getDateInMMMMyyyyFormat
import com.example.mobiledger.databinding.FragmentUpdateBudgetDialogBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class UpdateBudgetDialogFragment :
    BaseDialogFragment<FragmentUpdateBudgetDialogBinding, BaseNavigator>(R.layout.fragment_update_budget_dialog) {

    private val viewModel: UpdateBudgetViewModel by viewModels { viewModelFactory }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getSerializable(MONTH_YEAR)?.let {
                viewModel.monthYear = it as Calendar
            }
            getString(CATEGORY_NAME)?.let {
                viewModel.categoryName = it
            }
            getLong(OLD_BUDGET).let {
                viewModel.amount = it
            }
            getLong(KEY_MONTHLY_LIMIT).let {
                viewModel.monthlyLimit = it
            }
            getLong(KEY_TOTAL_BUDGET).let {
                viewModel.monthlyTotalBudget = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        initUI()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        viewBinding.apply {
            btnDelete.setOnClickListener {
                viewModel.updateBudgetAmount(-viewModel.amount)
            }

            btnUpdate.setOnClickListener {
                if (it.isEnabled && getAmount().isNotEmpty()) {
                    val amtChange = getAmount().toLong() - viewModel.amount
                    if (amtChange == 0L) dismiss()
                    else {
                        if (doValidations()) {
                            viewModel.updateBudgetAmount(amtChange)
                        }
                    }
                }
                doValidations()
            }

            amountTv.addTextChangedListener(amountTextWatcher)
        }
    }

    private fun initUI() {
        viewBinding.apply {
            updateBudgetHeading.text = getString(R.string.update_budget_heading, viewModel.categoryName)
            updateBudgetSubHeadingHeading.text = getDateInMMMMyyyyFormat(viewModel.monthYear)
            amountTv.setText(viewModel.amount.toString())
            viewBinding.btnUpdate.disable()
        }
    }

    private fun setUpObserver() {
        viewModel.loadingState.observe(viewLifecycleOwner, {
            if (it) {
                viewBinding.addBudgetProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.addBudgetProgressBar.visibility = View.GONE
            }
        })

        viewModel.updateResultLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            activityViewModel.updateBudgetResult()
            dismiss()
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                UpdateBudgetViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })
    }

    private fun getAmount(): String = viewBinding.amountTv.text.toString().trim()
    private fun isValidAmount(): Boolean = getAmount().isNotBlank() && getAmount().toLong() > 0L
    private fun isBudgetOverflow(): Boolean = viewModel.monthlyTotalBudget + getAmount().toLong() > viewModel.monthlyLimit


    private val amountTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidAmount()) {
                viewBinding.btnUpdate.enable()
                updateAmountViewBasedOnValidation(viewBinding.amountLayout, isValid = true)
            }
        }
    }

    /*---------------------------------------Validations------------------------------------------*/

    private fun doValidations(): Boolean {
        updateAmountViewBasedOnValidation(viewBinding.amountLayout, isValidAmount())
        return isValidAmount() && !isBudgetOverflow()
    }

    private fun updateAmountViewBasedOnValidation(
        textInputLayout: TextInputLayout,
        isValid: Boolean
    ) {
        if (isValid) {
            if (isBudgetOverflow()) textInputLayout.error =
                getString(R.string.budget_overflow_error, (viewModel.monthlyLimit - viewModel.monthlyTotalBudget).toString())
            else
                textInputLayout.error = null
        } else {
            textInputLayout.error = getString(R.string.field_required)
        }
    }

    companion object {
        private const val KEY_TOTAL_BUDGET = "totalBudget"
        private const val KEY_MONTHLY_LIMIT = "KEY_MONTHLY_LIMIT"
        private const val MONTH_YEAR = "MONTH_YEAR"
        private const val CATEGORY_NAME = "CATEGORY_NAME"
        private const val OLD_BUDGET = "AMOUNT"

        fun newInstance(monthYear: Calendar, categoryName: String, oldCategoryBudget: Long, monthlyBudgetData: MonthlyBudgetData) =
            UpdateBudgetDialogFragment().apply {

                arguments = Bundle().apply {
                    putSerializable(MONTH_YEAR, monthYear)
                    putString(CATEGORY_NAME, categoryName)
                    putLong(OLD_BUDGET, oldCategoryBudget)
                    putLong(KEY_MONTHLY_LIMIT, monthlyBudgetData.maxBudget)
                    putLong(KEY_TOTAL_BUDGET, monthlyBudgetData.totalBudget)
                }
            }
    }


}