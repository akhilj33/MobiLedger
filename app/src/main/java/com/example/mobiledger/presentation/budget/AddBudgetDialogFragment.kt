package com.example.mobiledger.presentation.budget

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.setWidthPercent
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.utils.showAddBudgetDialogFragment
import com.example.mobiledger.databinding.DialogFragmentAddBudgetBinding
import com.example.mobiledger.presentation.OneTimeObserver
import java.util.*

class AddBudgetDialogFragment :
    BaseDialogFragment<DialogFragmentAddBudgetBinding, BaseNavigator>
        (R.layout.dialog_fragment_add_budget) {

    private val viewModel: AddBudgetDialogViewModel by viewModels { viewModelFactory }

    private lateinit var categoty: String
    private var isCategoryBudget: Boolean = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel.getExpenseCategoryList()
        setWidthPercent(85)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isCategoryBudget = it.getBoolean(KEY_IS_CATEGORY_BUDGET)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        handleUI()
        setOnClickListener()
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
            dismiss()
        })
    }

    private fun setOnClickListener() {
        viewBinding.btnSeBudget.setOnClickListener {
            if (viewBinding.textAmount.text.toString().isNotEmpty()) {
                val amt = viewBinding.textAmount.text.toString().toLong()
                if (amt >= 0) {
//                    activity?.showDialog("Set Budget", "Are you sure you want to set the monthly budget?","Yes","No",onNegativeClick,onPositiveClick)
                    viewModel.setBudget(MonthlyBudgetData(amt, 0))
                }
            }
        }
    }

    private val onPositiveClick = fun() {

    }

    private val onNegativeClick = fun() {
        showAddBudgetDialogFragment(requireActivity().supportFragmentManager, false)
    }

    private fun initSpinner(categoryList: ArrayList<String>) {
        categoryList.sort()
        val adapter = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            categoryList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.spinnerCategory.adapter = adapter

        viewBinding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    (view as TextView).setTextColor(Color.BLACK)
                    (view).textSize = 16f
                    categoty = categoryList[position]
                }
            }
    }

    companion object {
        private const val KEY_IS_CATEGORY_BUDGET = "isCategoryBudget"
        fun newInstance(isCategoryBudget: Boolean) = AddBudgetDialogFragment().apply {
            arguments = Bundle().apply {
                putBoolean(KEY_IS_CATEGORY_BUDGET, isCategoryBudget)
            }
        }
    }

}