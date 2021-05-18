package com.example.mobiledger.presentation.recordtransaction

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.setWidthPercent
import com.example.mobiledger.common.showToast
import com.example.mobiledger.databinding.DialogFragmentAddTransactionBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.OneTimeObserver
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*


const val DATE_PATTERN = "dd/MM/yyyy"
const val MONTH_PATTERN = "MM-yyyy"

class AddTransactionDialogFragment :
    BaseDialogFragment<DialogFragmentAddTransactionBinding, BaseNavigator>
        (R.layout.dialog_fragment_add_transaction), DatePickerDialog.OnDateSetListener {

    private var incomeCategoryList = arrayListOf<String>()
    private var expenseCategoryList = arrayListOf<String>()
    private lateinit var categoty: String
    private lateinit var transactionType: TransactionType
    private var calendar: Calendar? = null
    private var dateFormat: SimpleDateFormat? = null
    private var date: String? = null
    private var monthYearFormat: SimpleDateFormat? = null
    private var monthYear: String? = null


    private val viewModel: AddTransactionDialogFragmentViewModel by viewModels { viewModelFactory }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getIncomeCategoryList()
        viewModel.getExpenseCategoryList()
        setWidthPercent(85)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToggle()
        initDate()
        setUpObserver()
    }

    private fun getCategoryList(transactionType: TransactionType) {
        val categoryList = if (transactionType == TransactionType.Income)
            incomeCategoryList
        else
            expenseCategoryList
        initSpinner(categoryList)
    }

    private fun initToggle() {
        transactionType = TransactionType.Expense

        viewBinding.incomeRadioButton.setOnClickListener {
            transactionType = TransactionType.Income
            viewBinding.incomeRadioButton.setTextColor(resources.getColor(R.color.colorTextLight))
            viewBinding.expanseRadioButton.setTextColor(resources.getColor(R.color.colorTextDark))
            getCategoryList(TransactionType.Income)
        }

        viewBinding.expanseRadioButton.setOnClickListener {
            transactionType = TransactionType.Expense
            viewBinding.expanseRadioButton.setTextColor(resources.getColor(R.color.colorTextLight))
            viewBinding.incomeRadioButton.setTextColor(resources.getColor(R.color.colorTextDark))
            getCategoryList(TransactionType.Expense)
        }
        viewBinding.btnSubmitTransaction.setOnClickListener {
            addTransaction()
        }
    }

    private fun setUpObserver() {
        viewModel.dataUpdatedResult.observe(
            viewLifecycleOwner,
            OneTimeObserver {
                activity?.showToast(getString(R.string.transaction_added))
                dialog?.dismiss()
            }
        )

        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewBinding.transactionProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.transactionProgressBar.visibility = View.GONE
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                AddTransactionDialogFragmentViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })

        viewModel.incomeCategoryList.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                incomeCategoryList = it.peekContent().incomeCategoryList as ArrayList<String>
            }
        })

        viewModel.expenseCategoryList.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                expenseCategoryList = it.peekContent().expenseCategoryList as ArrayList<String>
                getCategoryList(TransactionType.Expense)
            }
        })

    }

    private fun initDate() {
        calendar = Calendar.getInstance()
        dateFormat = SimpleDateFormat(DATE_PATTERN)
        date = dateFormat?.format(calendar?.time)
        viewBinding.textDate.text = date

        monthYearFormat = SimpleDateFormat(MONTH_PATTERN)
        monthYear = monthYearFormat?.format(calendar?.time)

        viewBinding.textDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val picker = DatePickerDialog(
            requireContext(), this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        picker.show()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        calendar?.set(year, month, day)
        dateFormat = SimpleDateFormat(DATE_PATTERN)
        date = dateFormat?.format(calendar?.time)
        viewBinding.textDate.text = date

        monthYearFormat = SimpleDateFormat(MONTH_PATTERN)
        monthYear = monthYearFormat?.format(calendar?.time)
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
