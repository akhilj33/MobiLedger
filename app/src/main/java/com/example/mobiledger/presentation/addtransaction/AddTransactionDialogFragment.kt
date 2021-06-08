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
import com.example.mobiledger.common.utils.AnimationDialogUtils
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.common.utils.DateUtils.getDateInDDMMMMyyyyFormat
import com.example.mobiledger.databinding.DialogFragmentAddTransactionBinding
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.OneTimeObserver
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
    private val viewModel: AddTransactionViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setOnClickListeners()
        initUI()
        (viewBinding.categorySpinnerTv as? AutoCompleteTextView)?.setAdapter(spinnerAdapter)
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
                showAnimatedDialog()
                clearAllFields()
            }
        )

        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewBinding.transactionProgressBar.visibility = View.VISIBLE
                isCancelable = false
            } else {
                viewBinding.transactionProgressBar.visibility = View.GONE
                isCancelable = true
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                AddTransactionViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })

        viewModel.categoryListLiveData.observe(viewLifecycleOwner, OneTimeObserver { it ->
            it.sort()
            spinnerAdapter.addItems(it)
        })

        viewModel.notificationIndicator.observe(viewLifecycleOwner, Observer {
            it.let {
                activityViewModel.notificationHandler(it)
            }
        })
    }

    private fun showAnimatedDialog() {
        AnimationDialogUtils.animatedDialog(requireActivity(), R.layout.animation_dialog_layout, 1500)
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

            viewBinding.dateTv.setOnClickListener {
                datePicker.show(requireActivity().supportFragmentManager, "tag");
            }

            btnSubmitTransaction.setOnClickListener {
                addTransaction()
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
    }

    private fun clearAllFields() {
        viewBinding.apply {
            transactionNameTv.setText("")
            transactionNameTv.clearFocus()
            transactionNameLayout.error = null

            amountTv.setText("")
            amountTv.clearFocus()
            amountLayout.error = null

            dateTv.setText(getDateInDDMMMMyyyyFormat(Timestamp.now()))
            dateTv.clearFocus()
            dateLayout.error = null

            descriptionTv.setText("")
            descriptionTv.clearFocus()

            categorySpinnerTv.setText("")
            categorySpinnerTv.clearFocus()
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

    private fun getName(): String = viewBinding.transactionNameTv.text.toString().trim()
    private fun getAmount(): String = viewBinding.amountTv.text.toString().trim()
    private fun getCategory(): String = viewBinding.categorySpinnerTv.text.toString()
    private fun getDate(): String = viewBinding.dateTv.text.toString()
    private fun getDescription(): String = viewBinding.descriptionTv.text.toString().trim()

    private fun isValidName(): Boolean = getName().isNotBlank()
    private fun isValidAmount(): Boolean = getAmount().isNotBlank() && getAmount().toLong() > 0L
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
                updateViewBasedOnValidation(viewBinding.transactionNameLayout, isValid = true)
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
        fun newInstance() = AddTransactionDialogFragment()
    }
}