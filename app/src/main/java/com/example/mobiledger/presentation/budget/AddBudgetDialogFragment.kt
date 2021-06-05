package com.example.mobiledger.presentation.budget

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
import com.example.mobiledger.common.extention.setWidthPercent
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.utils.AnimationDialogUtils
import com.example.mobiledger.databinding.DialogFragmentAddBudgetBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.addtransaction.SpinnerAdapter
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class AddBudgetDialogFragment :
    BaseDialogFragment<DialogFragmentAddBudgetBinding, BaseNavigator>
        (R.layout.dialog_fragment_add_budget) {

    private val viewModel: AddBudgetDialogViewModel by viewModels { viewModelFactory }

    private var isCategoryBudget: Boolean = true
    private val spinnerAdapter: SpinnerAdapter by lazy { SpinnerAdapter(requireContext()) }
    private var expenseCategoryList: ArrayList<String>? = null
    private var month: String = ""
    private var budgetTotal: Long = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setWidthPercent(85)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            expenseCategoryList = it.getStringArrayList(KEY_LIST)
            isCategoryBudget = it.getBoolean(KEY_IS_CATEGORY_BUDGET)
            month = it.getString(KEY_MONTH) as String
            budgetTotal = it.getLong(KEY_BUDGET_TOTAL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        handleUI()
        setOnClickListener()
        (viewBinding.categorySpinnerTv as? AutoCompleteTextView)?.setAdapter(spinnerAdapter)
        val spinnerExpenseList = expenseCategoryList?.sorted()
        spinnerAdapter.addItems(spinnerExpenseList as List<String>)
    }

    private fun handleUI() {
        if (isCategoryBudget) {
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
                !isCategoryBudget -> {
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
            viewModel.getMonthlyCategorySummary(getCategoryText(), getAmountText().toLong(), month, budgetTotal)
        }
    }

    private fun addBudgetOverview() {
        if (doValidations()) {
            viewModel.setBudget(MonthlyBudgetData(getAmountText().toLong(), budgetTotal), month)
        }
    }

    private fun getAmountText(): String = viewBinding.amountTv.text.toString()
    private fun getCategoryText(): String = viewBinding.categorySpinnerTv.text.toString()

    private fun isValidAmount(): Boolean = (getAmountText().isNotBlank() && getAmountText().toLong() >= 0)
    private fun isValidCategory(): Boolean = getCategoryText().isNotBlank()

    /*---------------------------------------Text Watchers-----------------------------------------*/

    private val amountTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidAmount()) {
                updateViewBasedOnValidation(viewBinding.amountLayout, isValid = true)
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
        if (isCategoryBudget) {
            updateViewBasedOnValidation(viewBinding.amountLayout, isValidAmount())
            updateViewBasedOnValidation(viewBinding.spinnerCategory, isValidCategory())
            return isValidCategory() && isValidAmount()
        } else {
            updateViewBasedOnValidation(viewBinding.amountLayout, isValidAmount())
            return isValidAmount()
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
        private const val KEY_LIST = "getList"
        private const val KEY_IS_CATEGORY_BUDGET = "isCategoryBudget"
        private const val KEY_MONTH = "month"
        private const val KEY_BUDGET_TOTAL = "budgetTotal"
        fun newInstance(isCategoryBudget: Boolean, list: List<String>, month: String, budgetTotal: Long) = AddBudgetDialogFragment().apply {
            arguments = Bundle().apply {
                putBoolean(KEY_IS_CATEGORY_BUDGET, isCategoryBudget)
                putStringArrayList(KEY_LIST, list as ArrayList<String>)
                putString(KEY_MONTH, month)
                putLong(KEY_BUDGET_TOTAL, budgetTotal)
            }
        }
    }

}