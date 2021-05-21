package com.example.mobiledger.presentation.budget

import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.setWidthPercent
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.databinding.DialogFragmentAddBudgetBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.addtransaction.SpinnerAdapter
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

    private fun getCategory(): String = viewBinding.mySpinnerDropdown.text.toString()

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
        (viewBinding.mySpinnerDropdown as? AutoCompleteTextView)?.setAdapter(spinnerAdapter)
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

    private fun setUpObserver() {
        viewModel.dataUpdatedResult.observe(viewLifecycleOwner, OneTimeObserver {
            activityViewModel.addBudgetResult()
            dismiss()
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
        viewBinding.btnSeBudget.setOnClickListener {

            if (!isCategoryBudget) {
                if (viewBinding.textAmount.text.toString().isNotEmpty()) {
                    val amt = viewBinding.textAmount.text.toString().toLong()
                    if (amt >= 0) {
                        viewModel.setBudget(MonthlyBudgetData(amt, budgetTotal), month)
                    }
                }
            } else {
                if (viewBinding.textAmount.text.toString().isNotEmpty() && getCategory().isNotEmpty()) {
                    val amt = viewBinding.textAmount.text.toString().toLong()
                    if (amt >= 0) {
                        viewModel.getMonthlyCategorySummary(getCategory(), amt, month, budgetTotal)
                    }
                }

            }
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