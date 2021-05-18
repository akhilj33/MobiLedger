package com.example.mobiledger.presentation.addtransaction

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.hideKeyboard
import com.example.mobiledger.common.showToast
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.databinding.DialogFragmentAddTransactionBinding
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.OneTimeObserver
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import java.util.*


class AddTransactionDialogFragment :
    BaseDialogFragment<DialogFragmentAddTransactionBinding, BaseNavigator>
        (R.layout.dialog_fragment_add_transaction) {

    private val spinnerAdapter: SpinnerAdapter by lazy { SpinnerAdapter(requireContext()) }
    private val viewModel: AddTransactionDialogFragmentViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setOnClickListeners()
        initUI()
        (viewBinding.mySpinnerDropdown as? AutoCompleteTextView)?.setAdapter(spinnerAdapter)
    }

    private fun initUI() {
        if (viewModel.transactionType == TransactionType.Income) {
            handleIncomeClick()
        } else {
            handleExpenseClick()
        }
    }

    private fun setUpObserver() {
        viewModel.dataUpdatedResult.observe(
            viewLifecycleOwner,
            OneTimeObserver {
                activityViewModel.addTransactionResult()
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

        viewModel.categoryListLiveData.observe(viewLifecycleOwner, OneTimeObserver { it ->
            spinnerAdapter.addItems(it)
        })
    }

    private fun setOnClickListeners() {
        viewBinding.apply {
            toggleIncome.setOnClickListener {
                clearAllFields()
                viewModel.transactionType = TransactionType.Income
                handleIncomeClick()
            }
            toggleExpense.setOnClickListener {
                clearAllFields()
                viewModel.transactionType = TransactionType.Expense
                handleExpenseClick()
            }

            viewBinding.textDate.setOnClickListener {
                datePicker.show(requireActivity().supportFragmentManager, "tag");
            }

            btnSubmitTransaction.setOnClickListener {
                addTransaction()
            }
        }

        datePicker.addOnPositiveButtonClickListener {
            viewBinding.textDate.setText(datePicker.headerText)
            viewModel.timeInMillis = it
        }

        viewBinding.mySpinnerDropdown.addTextChangedListener(categoryTextWatcher)
        viewBinding.textAmount.addTextChangedListener(amountTextWatcher)
        viewBinding.textName.addTextChangedListener(nameTextWatcher)
        viewBinding.textDate.addTextChangedListener(dateTextWatcher)
    }

    private fun clearAllFields() {
        viewBinding.apply {
            textName.setText("")
            textName.clearFocus()
            nameLayout.error = null

            textAmount.setText("")
            textAmount.clearFocus()
            amountLayout.error = null

            textDate.setText("")
            textDate.clearFocus()
            dateLayout.error = null

            textDescription.setText("")
            textDescription.clearFocus()

            mySpinnerDropdown.setText("")
            mySpinnerDropdown.clearFocus()
            spinnerCategory.error = null

        }
        viewBinding.root.hideKeyboard()
    }

    private fun handleExpenseClick() {
        updateToggleButton()
        viewModel.getExpenseCategoryList()
    }

    private fun handleIncomeClick() {
        updateToggleButton()
        viewModel.getIncomeCategoryList()
    }

    private fun updateToggleButton() {
        if (viewModel.transactionType == TransactionType.Income) {
            updateSelectedTab(TransactionType.Income)
            updateNotSelectedTab(TransactionType.Expense)
        } else if (viewModel.transactionType == TransactionType.Expense) {
            updateSelectedTab(TransactionType.Expense)
            updateNotSelectedTab(TransactionType.Income)
        }
    }

    private fun updateNotSelectedTab(transactionType: TransactionType) {
        val tab = if (transactionType == TransactionType.Income) viewBinding.toggleIncome
        else viewBinding.toggleExpense

        TextViewCompat.setTextAppearance(tab, R.style.GelionRegularGrey16)
        tab.elevation = resources.getDimension(R.dimen.cdp_0)
        tab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorLightGrey)
        tab.icon = null
        tab.stateListAnimator = null
    }

    private fun updateSelectedTab(transactionType: TransactionType) {
        val tab = if (transactionType == TransactionType.Income) viewBinding.toggleIncome
        else viewBinding.toggleExpense

        TextViewCompat.setTextAppearance(tab, R.style.GelionRegularNavyBlue16)
        tab.elevation = resources.getDimension(R.dimen.cdp_2)
        tab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.selected_icon)
        tab.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorAppBlue)
    }

    private val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build()

    private val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTitleText("Select date")
            .setCalendarConstraints(constraintsBuilder)
            .build()

    private fun getName(): String = viewBinding.textName.text.toString()
    private fun getAmount(): String = viewBinding.textAmount.text.toString()
    private fun getCategory(): String = viewBinding.mySpinnerDropdown.text.toString()
    private fun getDate(): String = viewBinding.textDate.text.toString()
    private fun getDescription(): String = viewBinding.textDescription.text.toString()

    private fun isValidName(): Boolean = getName().isNotBlank()
    private fun isValidAmount(): Boolean = getAmount().isNotBlank()
    private fun isValidCategory(): Boolean = getCategory().isNotBlank()
    private fun isValidDate(): Boolean = getDate().isNotBlank()

    private fun addTransaction() {
        if (doValidations()) {
            val milliSeconds = viewModel.timeInMillis ?: DateUtils.getCurrentDate().timeInMillis
            val date = Date(milliSeconds)
            val monthYear = DateUtils.getDateInMMyyyyFormat(DateUtils.getCalendarFromMillis(milliSeconds))
            val transactionEntity = TransactionEntity(
                getName(), getAmount().toLong(), getCategory(), getDescription(),
                viewModel.transactionType, Timestamp(date)
            )
            viewModel.addTransaction(monthYear, transactionEntity)
        }
    }

    /*---------------------------------------Text Watchers-----------------------------------------*/

    private val nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidName()) {
                updateViewBasedOnValidation(viewBinding.nameLayout, isValid = true)
            }
        }
    }

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

    private val dateTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidDate()) {
                updateViewBasedOnValidation(viewBinding.dateLayout, isValid = true)
            }
        }
    }

    /*---------------------------------------Validations------------------------------------------*/

    private fun doValidations(): Boolean {
        updateViewBasedOnValidation(viewBinding.nameLayout, isValidName())
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
            textInputLayout.error = getString(R.string.field_required)
        }
    }


    companion object {
        fun newInstance() = AddTransactionDialogFragment()
    }
}