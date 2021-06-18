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
import com.example.mobiledger.common.extention.disable
import com.example.mobiledger.common.extention.enable
import com.example.mobiledger.common.extention.invisible
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.utils.DateUtils
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
import com.google.firebase.Timestamp
import java.util.*

class TransactionDetailDialogFragment :
    BaseDialogFragment<TransactionDetailFragmentBinding, BaseNavigator>(R.layout.transaction_detail_fragment) {

    private val spinnerAdapter: SpinnerAdapter by lazy { SpinnerAdapter(requireContext()) }
    private val viewModel: TransactionDetailViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(TRANSACTION_ENTITY)?.let {
                viewModel.oldTransactionEntity = convertJsonStringToObject<TransactionEntity>(it) ?: TransactionEntity()
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
            if (viewModel.oldTransactionEntity.transactionType == TransactionType.Income) {
                toggleTransactionType.text = getString(R.string.income)
                viewModel.getIncomeCategoryList()
            } else {
                toggleTransactionType.text = getString(R.string.expense)
                viewModel.getExpenseCategoryList()
            }
            categorySpinnerTv.setText(viewModel.oldTransactionEntity.category)
            transactionNameTv.setText(viewModel.oldTransactionEntity.name)
            amountTv.setText(viewModel.oldTransactionEntity.amount.toString())
            amountTv.setText(viewModel.oldTransactionEntity.amount.toString())
            dateTv.setText(getDateInDDMMMMyyyyFormat(viewModel.oldTransactionEntity.transactionTime))
            descriptionTv.setText(viewModel.oldTransactionEntity.description)
            viewBinding.btnUpdate.disable()
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

        viewModel.categoryListLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            spinnerAdapter.addItems(it)
        })

        viewModel.updateResultLiveData.observe(viewLifecycleOwner, OneTimeObserver{
            activityViewModel.updateTransactionResult()
            dismiss()
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                TransactionDetailViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })

        viewModel.categoryListLiveData.observe(viewLifecycleOwner, OneTimeObserver { it ->
            spinnerAdapter.addItems(it)
        })
    }

    private fun setOnClickListeners() {
        viewBinding.apply {
            dateTv.setOnClickListener {
                datePicker.show(requireActivity().supportFragmentManager, "tag");
            }

            closeIv.setOnClickListener {
                dismiss()
            }

            btnDelete.setOnClickListener {
                viewModel.deleteTransaction()
            }

            btnUpdate.setOnClickListener {
                if (it.isEnabled) {
                    if (checkAllFieldsSame()) dismiss()
                    else {
                        if (doValidations()) {
                            val timeStamp: Timestamp = if (viewModel.timeInMillis != null) {
                                Timestamp(Date(viewModel.timeInMillis as Long))
                            } else {
                                viewModel.oldTransactionEntity.transactionTime
                            }
                            val newTransactionEntity = TransactionEntity(
                                getName(), getAmount().toLong(), getCategory(), getDescription(),
                                viewModel.oldTransactionEntity.transactionType, timeStamp
                            ).apply { id = viewModel.oldTransactionEntity.id }
                            handleFieldChanges(newTransactionEntity)
                        }
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

    private fun handleFieldChanges(newTransactionEntity: TransactionEntity) {
        val monthYear =
            DateUtils.getDateInMMyyyyFormat(DateUtils.getCalendarFromMillis(newTransactionEntity.transactionTime.toDate().time))
        when {
            !isMonthYearSame(newTransactionEntity.transactionTime) -> {
                viewModel.updateDatabaseOnMonthYearChanged(monthYear, newTransactionEntity)
            }
            !isCategorySame(newTransactionEntity.category) -> {
                viewModel.updateDatabaseOnCategoryChanged(monthYear, newTransactionEntity)
            }
            !isAmountSame(newTransactionEntity.amount) -> {
                viewModel.updateDatabaseOnAmountChanged(monthYear, newTransactionEntity)
            }
            else -> {
                viewModel.updateDatabaseOnOtherFieldChanged(monthYear, newTransactionEntity)
            }
        }
    }

    private fun checkAllFieldsSame(): Boolean {
        viewModel.oldTransactionEntity.apply {
            return amount.toString() == getAmount() && category == getCategory() && name == getName() &&
                    description == getDescription() && getDateInDDMMMMyyyyFormat(transactionTime) == getDate()
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
    private fun isValidAmount(): Boolean = getAmount().isNotBlank() && getAmount().toLong() > 0L
    private fun isValidCategory(): Boolean = getCategory().isNotBlank()
    private fun isValidDate(): Boolean = getDate().isNotBlank()
    
    private fun isMonthYearSame(date: Timestamp): Boolean {
        val oldMonthYear =
            DateUtils.getDateInMMyyyyFormat(DateUtils.getCalendarFromMillis(viewModel.oldTransactionEntity.transactionTime.toDate().time))
        val newMonthYear = DateUtils.getDateInMMyyyyFormat(DateUtils.getCalendarFromMillis(date.toDate().time))
        return oldMonthYear==newMonthYear
    }
    private fun isCategorySame(category: String): Boolean = viewModel.oldTransactionEntity.category == category
    private fun isAmountSame(amount: Long): Boolean = viewModel.oldTransactionEntity.amount == amount

    /*---------------------------------------Text Watchers-----------------------------------------*/

    private val nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidName()) {
                viewBinding.btnUpdate.enable()
                updateViewBasedOnValidation(viewBinding.transactionNameLayout, isValid = true)
            }
        }
    }

    private val amountTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidAmount()) {
                viewBinding.btnUpdate.enable()
                updateViewBasedOnValidation(viewBinding.amountLayout, isValid = true)
            }
        }
    }

    private val categoryTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidCategory()) {
                viewBinding.btnUpdate.enable()
                updateViewBasedOnValidation(viewBinding.spinnerCategory, isValid = true)
            }
        }
    }

    private val dateTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidDate()) {
                viewBinding.btnUpdate.enable()
                updateViewBasedOnValidation(viewBinding.dateLayout, isValid = true)
            }
        }
    }

    private val descriptionTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            viewBinding.btnUpdate.enable()
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