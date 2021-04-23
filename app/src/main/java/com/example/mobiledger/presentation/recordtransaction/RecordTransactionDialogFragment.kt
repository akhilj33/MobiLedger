package com.example.mobiledger.presentation.recordtransaction

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
import com.example.mobiledger.common.extention.setWidthPercent
import com.example.mobiledger.databinding.DialogFragmentRecordTransactionBinding
import com.example.mobiledger.domain.enums.TransactionType
import java.util.*

class RecordTransactionDialogFragment :
    BaseDialogFragment<DialogFragmentRecordTransactionBinding, BaseNavigator>
        (R.layout.dialog_fragment_record_transaction) {

    private var categoryList = arrayListOf<String>()
    private lateinit var categoty: String
    private lateinit var transactionType: TransactionType

    private val viewModel: RecordTransactionDialogFragmentViewModel by viewModels { viewModelFactory }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setWidthPercent(85)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCategoryList()
        initToggle()
    }

    private fun getCategoryList() {
        categoryList = viewModel.provideCategoryList()
        initSpinner()
    }

    private fun initToggle() {
        transactionType = TransactionType.Expense
        viewBinding.incomeRadioButton.setOnClickListener {
            viewBinding.incomeRadioButton.setTextColor(resources.getColor(R.color.colorTextLight))
            viewBinding.expanseRadioButton.setTextColor(resources.getColor(R.color.colorTextDark))
        }
        viewBinding.expanseRadioButton.setOnClickListener {
            transactionType = TransactionType.Expense
            viewBinding.expanseRadioButton.setTextColor(resources.getColor(R.color.colorTextLight))
            viewBinding.incomeRadioButton.setTextColor(resources.getColor(R.color.colorTextDark))
        }
        viewBinding.btnSubmitTransaction.setOnClickListener {

        }
    }


    private fun initSpinner() {
        Collections.sort(categoryList)
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
        fun newInstance() = RecordTransactionDialogFragment()
    }
}