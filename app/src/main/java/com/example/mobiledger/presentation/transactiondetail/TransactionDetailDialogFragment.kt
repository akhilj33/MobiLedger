package com.example.mobiledger.presentation.transactiondetail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.invisible
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.utils.DateUtils.getDateInDDMMMMyyyyFormat
import com.example.mobiledger.common.utils.JsonUtils.convertJsonStringToObject
import com.example.mobiledger.common.utils.JsonUtils.convertToJsonString
import com.example.mobiledger.databinding.TransactionDetailFragmentBinding
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.addtransaction.SpinnerAdapter
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout

class TransactionDetailDialogFragment :
    BaseDialogFragment<TransactionDetailFragmentBinding, BaseNavigator>(R.layout.transaction_detail_fragment) {

    private val spinnerAdapter: SpinnerAdapter by lazy { SpinnerAdapter(requireContext()) }
    private val viewModel: TransactionDetailViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(TRANSACTION_ENTITY)?.let {
                viewModel.transactionEntity = convertJsonStringToObject<TransactionEntity>(it)?: TransactionEntity()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setOnClickListeners()
        initUI()
        (viewBinding.categorySpinnerTv as? AutoCompleteTextView)?.setAdapter(spinnerAdapter)
    }

    private fun initUI() {
        viewBinding.apply {
            if (viewModel.transactionEntity.transactionType == TransactionType.Income) {
                toggleTransactionType.text = getString(R.string.income)
                viewModel.getIncomeCategoryList()
            } else {
                toggleTransactionType.text = getString(R.string.expense)
                viewModel.getExpenseCategoryList()
            }
            categorySpinnerTv.setText(viewModel.transactionEntity.category)
            transactionNameTv.setText(viewModel.transactionEntity.name)
            amountTv.setText(viewModel.transactionEntity.amount.toString())
            amountTv.setText(viewModel.transactionEntity.amount.toString())
            dateTv.setText(getDateInDDMMMMyyyyFormat(viewModel.transactionEntity.transactionTime))
            descriptionTv.setText(viewModel.transactionEntity.description)
            viewBinding.btnUpdate.invisible()
        }
    }

    private fun setUpObserver() {

        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewBinding.transactionProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.transactionProgressBar.visibility = View.GONE
            }
        })

        viewModel.categoryListLiveData.observe(viewLifecycleOwner, OneTimeObserver { it ->
            spinnerAdapter.addItems(it)
        })

//        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
//            when (it.viewErrorType) {
//                AddTransactionDialogFragmentViewModel.ViewErrorType.NON_BLOCKING -> {
//                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
//                }
//            }
//        })
//
//        viewModel.categoryListLiveData.observe(viewLifecycleOwner, OneTimeObserver { it ->
//            spinnerAdapter.addItems(it)
//        })
    }

    private fun setOnClickListeners() {
        viewBinding.apply {
            dateTv.setOnClickListener {
                datePicker.show(requireActivity().supportFragmentManager, "tag");
            }

            closeIv.setOnClickListener {
                dismiss()
            }

            btnUpdate.setOnClickListener {
                if(checkAllFieldsSame()) dismiss()
                else{
                    if (doValidations()){

                    }
                }
            }

        }

        datePicker.addOnPositiveButtonClickListener {
            viewBinding.dateTv.setText(datePicker.headerText)
            viewModel.timeInMillis = it
        }

        viewBinding.categorySpinnerTv.addTextChangedListener(categoryTextWatcher)
        viewBinding.amountTv.addTextChangedListener(amountTextWatcher)
        viewBinding.transactionNameTv.addTextChangedListener(nameTextWatcher)
        viewBinding.dateTv.addTextChangedListener(dateTextWatcher)
        viewBinding.descriptionTv.addTextChangedListener(descriptionTextWatcher)
    }

    private fun checkAllFieldsSame(): Boolean {
        viewModel.transactionEntity.apply {
            return amount.toString() == getAmount() && category == getCategory() && name == getName() &&
                    description==getDescription() && getDateInDDMMMMyyyyFormat(transactionTime)==getDate()
        }
    }

    private val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build()

    private val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setCalendarConstraints(constraintsBuilder)
            .build()

    private fun getName(): String = viewBinding.transactionNameTv.text.toString().trim()
    private fun getAmount(): String = viewBinding.amountTv.text.toString().trim()
    private fun getCategory(): String = viewBinding.categorySpinnerTv.text.toString()
    private fun getDate(): String = viewBinding.dateTv.text.toString()
    private fun getDescription(): String = viewBinding.descriptionTv.text.toString().trim()

    private fun isValidName(): Boolean = getName().isNotBlank()
    private fun isValidAmount(): Boolean = getAmount().isNotBlank()
    private fun isValidCategory(): Boolean = getCategory().isNotBlank()
    private fun isValidDate(): Boolean = getDate().isNotBlank()


    /*---------------------------------------Text Watchers-----------------------------------------*/

    private val nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidName()) {
                viewBinding.btnUpdate.visible()
                updateViewBasedOnValidation(viewBinding.transactionNameLayout, isValid = true)
            }
        }
    }

    private val amountTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidAmount()) {
                viewBinding.btnUpdate.visible()
                updateViewBasedOnValidation(viewBinding.amountLayout, isValid = true)
            }
        }
    }

    private val categoryTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidCategory()) {
                viewBinding.btnUpdate.visible()
                updateViewBasedOnValidation(viewBinding.spinnerCategory, isValid = true)
            }
        }
    }

    private val dateTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidDate()) {
                viewBinding.btnUpdate.visible()
                updateViewBasedOnValidation(viewBinding.dateLayout, isValid = true)
            }
        }
    }

    private val descriptionTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            viewBinding.btnUpdate.visible()
        }
    }

    /*---------------------------------------Validations------------------------------------------*/

    private fun doValidations(): Boolean {
        updateViewBasedOnValidation(viewBinding.transactionNameLayout, isValidName())
        updateViewBasedOnValidation(viewBinding.amountLayout, isValidAmount())
        updateViewBasedOnValidation(viewBinding.spinnerCategory, isValidCategory())
        updateViewBasedOnValidation(viewBinding.dateLayout, isValidDate())

        return isValidName() && isValidCategory() && isValidAmount() && isValidDate()
    }

    private fun updateViewBasedOnValidation(
        textInputLayout: TextInputLayout,
        isValid: Boolean
    ) {
        if (isValid) {
            textInputLayout.error = null
        } else {
            textInputLayout.error = getString(R.string.field_invalid)
        }
    }

    companion object {
        private const val TRANSACTION_ENTITY = "transaction_entity"

        fun newInstance(transactionEntity: TransactionEntity) = TransactionDetailDialogFragment().apply {
            arguments = Bundle().apply {
                putString(TRANSACTION_ENTITY, convertToJsonString(transactionEntity))
            }
        }
    }

}