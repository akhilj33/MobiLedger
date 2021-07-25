package com.example.mobiledger.presentation.budgetTemplate

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.*
import com.example.mobiledger.databinding.DialogFragmentAddBudgetBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.addtransaction.SpinnerAdapter
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class EditBudgetTemplateDialogFragment :
    BaseDialogFragment<DialogFragmentAddBudgetBinding, BaseNavigator>
        (R.layout.dialog_fragment_add_budget) {

    private val viewModel: EditBudgetTemplateDialogViewModel by viewModels { viewModelFactory }
    private val spinnerAdapter: SpinnerAdapter by lazy { SpinnerAdapter(requireContext()) }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.isAddCategory = it.getBoolean(KEY_IS_ADD_CATEGORY)
            viewModel.category = it.getString(KEY_CATEGORY).toString()
            viewModel.expenseCategoryList = it.getStringArrayList(KEY_LIST) as ArrayList<String>
            viewModel.budgetTotal = it.getLong(KEY_BUDGET_TOTAL)
            viewModel.maxLimit = it.getLong(KEY_MONTHLY_LIMIT)
            viewModel.id = it.getString(KEY_TEMPLATE_ID).toString()
            viewModel.oldBudget = it.getLong(KEY_OLD_BUDGET)
            viewModel.isUpdateMaxLimit = it.getBoolean(KEY_IS_UPDATE_MAX_LIMIT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        handleUI()
        setOnClickListener()
        if (viewModel.isUpdateMaxLimit) {
            viewBinding.amountTv.setText(viewModel.maxLimit.toString())
            viewBinding.btnUpdate.disable()
        }
        else {
            (viewBinding.categorySpinnerTv as? AutoCompleteTextView)?.setAdapter(spinnerAdapter)
            val spinnerExpenseList = viewModel.expenseCategoryList.sorted()
            spinnerAdapter.addItems(spinnerExpenseList)
        }
    }

    private fun handleUI() {
        if (viewModel.isAddCategory) {
            viewBinding.apply {
                spinnerCategory.visible()
                btnDelete.gone()
                btnUpdate.gone()
                btnSeBudget.visible()
            }
        } else {
            if (viewModel.isUpdateMaxLimit) {
                viewBinding.apply {
                    textSetYourBudget.text = requireContext().getString(R.string.update_your_budget)
                    spinnerCategory.gone()
                    btnUpdate.visible()
                    btnDelete.gone()
                    btnSeBudget.gone()
                }
            } else {
                viewBinding.apply {
                    textSetYourBudget.text = requireContext().getString(R.string.update_your_budget)
                    spinnerCategory.gone()
                    btnUpdate.visible()
                    btnDelete.visible()
                    btnSeBudget.gone()
                }
            }
        }
    }

    private fun setUpObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, {
            if (it) {
                viewBinding.addBudgetProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.addBudgetProgressBar.visibility = View.GONE
            }
        })

        viewModel.dataUpdatedResult.observe(viewLifecycleOwner, OneTimeObserver {
            activityViewModel.updateUpdateBudgetFragment()
            dismiss()
        })

        viewModel.dataAdded.observe(viewLifecycleOwner, {
            if (it) {
                activityViewModel.updateUpdateBudgetFragment()
                dismiss()
            }
        })
    }

    private fun setOnClickListener() {
        viewBinding.categorySpinnerTv.addTextChangedListener(categoryTextWatcher)
        viewBinding.amountTv.addTextChangedListener(amountTextWatcher)

        viewBinding.btnDelete.setOnSafeClickListener {
            viewModel.deleteBudgetTemplateCategory()
        }


        viewBinding.btnUpdate.setOnSafeClickListener {
            if (it.isEnabled){
                if (viewModel.isUpdateMaxLimit) {
                    if (!viewModel.isAddCategory) {
                        if (doValidations()) {
                            viewModel.updateBudgetTemplateMaxLimit(getAmountText().toLong())
                        }
                    }
                } else {
                    if (!viewModel.isAddCategory && getAmountText().isNotEmpty()) {
                        val amtChange = getAmountText().toLong() - viewModel.oldBudget
                        if (amtChange == 0L)
                            dismiss()
                        else {
                            if (doValidations()) {
                                updateCategory(amtChange)
                            }
                        }
                    }
                }
                doValidations()
            }
        }

        viewBinding.btnSeBudget.setOnSafeClickListener {
            if (it.isEnabled && viewModel.isAddCategory) {
                if (doValidations()) {
                    addCategoryBudget()
                }
            }
        }
    }

    private fun updateCategory(amtChange: Long) {
        viewModel.updateBudgetTemplateCategoryAmount(amtChange)
    }

    private fun addCategoryBudget() {
        viewModel.addNewBudgetTemplateCategory(getCategoryText(), getAmountText().toLong())
    }

    private fun getAmountText(): String = viewBinding.amountTv.text.toString()
    private fun getCategoryText(): String = viewBinding.categorySpinnerTv.text.toString()

    private fun isValidAmount(): Boolean = (getAmountText().isNotBlank() && getAmountText().toLong() >= 0)
    private fun isValidCategory(): Boolean = getCategoryText().isNotBlank()
    private fun isBudgetOverflow(): Boolean = viewModel.budgetTotal + getAmountText().toLong() > viewModel.maxLimit
    private fun isMaxLimitChanged(): Boolean = viewModel.maxLimit!=getAmountText().toLong()

/*---------------------------------------Text Watchers-----------------------------------------*/

    private val amountTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidAmount()) {
                if (viewModel.isUpdateMaxLimit && !isMaxLimitChanged()) viewBinding.btnUpdate.disable()
                else{
                    viewBinding.btnUpdate.enable()
                    updateAmountViewBasedOnValidation(viewBinding.amountLayout, isValid = true)
                }
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
        return if (viewModel.isAddCategory) {
            updateAmountViewBasedOnValidation(viewBinding.amountLayout, isValidAmount())
            updateViewBasedOnValidation(viewBinding.spinnerCategory, isValidCategory())
            isValidCategory() && isValidAmount() && !isBudgetOverflow()
        } else {
            if (viewModel.isUpdateMaxLimit) {
                updateViewBasedOnValidation(viewBinding.amountLayout, isValidAmount())
                isValidAmount()
            } else {
                updateViewBasedOnValidation(viewBinding.amountLayout, isValidAmount())
                isValidAmount() && !isBudgetOverflow()
            }
        }
    }

    private fun updateAmountViewBasedOnValidation(
        textInputLayout: TextInputLayout,
        isValid: Boolean
    ) {
        if (isValid) {
            if (viewModel.isAddCategory && isBudgetOverflow()) {
                textInputLayout.error = getString(R.string.budget_overflow_error, (viewModel.maxLimit - viewModel.budgetTotal).toString())
            } else textInputLayout.error = null
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
        private const val KEY_CATEGORY = "KEY_CATEGORY"
        private const val KEY_LIST = "KEY_LIST"
        private const val KEY_MONTHLY_LIMIT = "KEY_MONTHLY_LIMIT"
        private const val KEY_BUDGET_TOTAL = "BUDGET_TOTAL"
        private const val KEY_TEMPLATE_ID = "TEMPLATE_ID"
        private const val KEY_OLD_BUDGET = "OLD_BUDGET"
        private const val KEY_IS_UPDATE_MAX_LIMIT = "IS_UPDATE_MAX_LIMIT"

        fun newInstance(
            templateId: String,
            isAddCategory: Boolean,
            category: String,
            oldBudget: Long,
            list: List<String>,
            maxLimit: Long,
            totalBudget: Long,
            isUpdateMaxLimit: Boolean
        ) =
            EditBudgetTemplateDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(KEY_IS_ADD_CATEGORY, isAddCategory)
                    putString(KEY_CATEGORY, category)
                    putLong(KEY_OLD_BUDGET, oldBudget)
                    putStringArrayList(KEY_LIST, list as ArrayList<String>)
                    putLong(KEY_BUDGET_TOTAL, totalBudget)
                    putLong(KEY_MONTHLY_LIMIT, maxLimit)
                    putString(KEY_TEMPLATE_ID, templateId)
                    putBoolean(KEY_IS_UPDATE_MAX_LIMIT, isUpdateMaxLimit)
                }
            }
    }
}