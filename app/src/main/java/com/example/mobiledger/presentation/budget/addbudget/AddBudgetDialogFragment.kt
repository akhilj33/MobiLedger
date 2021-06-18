package com.example.mobiledger.presentation.budget.addbudget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.utils.AnimationDialogUtils
import com.example.mobiledger.databinding.DialogFragmentAddBudgetBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.addtransaction.SpinnerAdapter
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class AddBudgetDialogFragment :
    BaseDialogFragment<DialogFragmentAddBudgetBinding, BaseNavigator>
        (R.layout.dialog_fragment_add_budget) {

    private val viewModel: AddBudgetDialogViewModel by viewModels { viewModelFactory }
    private val spinnerAdapter: SpinnerAdapter by lazy { SpinnerAdapter(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.expenseCategoryList = it.getStringArrayList(KEY_LIST) as ArrayList<String>
            viewModel.monthlyLimit = it.getLong(KEY_MONTHLY_LIMIT)
            viewModel.month = it.getString(KEY_MONTH) as String
            viewModel.budgetTotal = it.getLong(KEY_BUDGET_TOTAL)
            viewModel.isCategoryBudget = it.getBoolean(KEY_IS_ADD_CATEGORY)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        handleUI()
        setOnClickListener()
        (viewBinding.categorySpinnerTv as? AutoCompleteTextView)?.setAdapter(spinnerAdapter)
        val spinnerExpenseList = viewModel.expenseCategoryList.sorted()
        spinnerAdapter.addItems(spinnerExpenseList)
    }

    private fun handleUI() {
        if (viewModel.isCategoryBudget) {
            viewBinding.spinnerCategory.visible()
        } else {
            viewBinding.spinnerCategory.gone()
        }
    }

    private fun showAnimatedDialog() {
        AnimationDialogUtils.animatedDialog(requireActivity(), R.layout.animation_dialog_layout, 1500)
        dismiss()
    }

    private fun setUpObserver() {
        viewModel.dataUpdatedResult.observe(viewLifecycleOwner, OneTimeObserver {
            activityViewModel.addBudgetResult()
            showAnimatedDialog()
        })

        viewModel.isLoading.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                viewBinding.addBudgetProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.addBudgetProgressBar.visibility = View.GONE
            }
        })
    }

    private fun setOnClickListener() {
        viewBinding.categorySpinnerTv.addTextChangedListener(categoryTextWatcher)
        viewBinding.amountTv.addTextChangedListener(amountTextWatcher)

        viewBinding.btnSeBudget.setOnClickListener {

            when {
                !viewModel.isCategoryBudget -> {
                    addBudgetOverview()
                }
                else -> {
                    addCategoryBudget()
                }
            }
        }
    }

    private fun addCategoryBudget() {
        if (doValidations()) {
            viewModel.getMonthlyCategorySummary(getCategoryText(), getAmountText().toLong())
        }
    }

    private fun addBudgetOverview() {
        if (doValidations()) {
            viewModel.setMonthlyBudgetLimit(MonthlyBudgetData(getAmountText().toLong(), viewModel.budgetTotal))
        }
    }

    private fun getAmountText(): String = viewBinding.amountTv.text.toString()
    private fun getCategoryText(): String = viewBinding.categorySpinnerTv.text.toString()

    private fun isValidAmount(): Boolean = (getAmountText().isNotBlank() && getAmountText().toLong() >= 0)
    private fun isValidCategory(): Boolean = getCategoryText().isNotBlank()
    private fun isBudgetOverflow():Boolean = viewModel.budgetTotal+getAmountText().toLong() > viewModel.monthlyLimit

    /*---------------------------------------Text Watchers-----------------------------------------*/

    private val amountTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidAmount()) {
                updateAmountViewBasedOnValidation(viewBinding.amountLayout, isValid = true)
            }
        }
    }

    private val categoryTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidCategory()) {
                updateViewBasedOnValidation(viewBinding.spinnerCategory, isValid = true)
            }
        }
    }


    /*---------------------------------------Validations------------------------------------------*/

    private fun doValidations(): Boolean {
        return if (viewModel.isCategoryBudget) {
            updateAmountViewBasedOnValidation(viewBinding.amountLayout, isValidAmount())
            updateViewBasedOnValidation(viewBinding.spinnerCategory, isValidCategory())
            isValidCategory() && isValidAmount() && !isBudgetOverflow()
        } else {
            updateViewBasedOnValidation(viewBinding.amountLayout, isValidAmount())
            isValidAmount()
        }
    }

    private fun updateAmountViewBasedOnValidation(
        textInputLayout: TextInputLayout,
        isValid: Boolean
    ) {
        if (isValid) {
            if (viewModel.isCategoryBudget && isBudgetOverflow()){
                textInputLayout.error = getString(R.string.budget_overflow_error, (viewModel.monthlyLimit - viewModel.budgetTotal).toString())
            }
            else textInputLayout.error = null
        } else {
            textInputLayout.error = getString(R.string.field_required)
        }
    }

    private fun updateViewBasedOnValidation(
        textInputLayout: TextInputLayout,
        isValid: Boolean
    ) {
        if (isValid) {
            textInputLayout.error = null
        } else {
            textInputLayout.error = getString(R.string.field_required)
        }
    }

    companion object {
        private const val KEY_IS_ADD_CATEGORY = "KEY_IS_ADD_CATEGORY"
        private const val KEY_LIST = "getList"
        private const val KEY_MONTHLY_LIMIT = "KEY_MONTHLY_LIMIT"
        private const val KEY_MONTH = "month"
        private const val KEY_BUDGET_TOTAL = "budgetTotal"
        fun newInstance(monthlyLimit: Long, list: List<String>, month: String, budgetTotal: Long, isAddCategory: Boolean) =
            AddBudgetDialogFragment().apply {
            arguments = Bundle().apply {
                putLong(KEY_MONTHLY_LIMIT, monthlyLimit)
                putStringArrayList(KEY_LIST, list as ArrayList<String>)
                putString(KEY_MONTH, month)
                putLong(KEY_BUDGET_TOTAL, budgetTotal)
                putBoolean(KEY_IS_ADD_CATEGORY, isAddCategory)
            }
        }
    }
}