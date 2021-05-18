package com.example.mobiledger.presentation.categoryFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.databinding.DialogFragmentAddCategoryBinding
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.OneTimeObserver
import java.util.*

class AddCategoryDialogFragment :
    BaseDialogFragment<DialogFragmentAddCategoryBinding, BaseNavigator>
        (R.layout.dialog_fragment_add_category) {

    private val viewModel: AddCategoryDialogViewModel by viewModels { viewModelFactory }

    private var oldList: ArrayList<String>? = null
    private var isIncomeCategory: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            oldList = it.getStringArrayList(KEY_LIST)
            isIncomeCategory = it.getBoolean(KEY_IS_INCOME)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        initUI()
    }

    private fun initUI() {
        setOnClickListener()

        if (isIncomeCategory) viewBinding.textAddCategory.text = getString(R.string.add_new_income_category)
        else viewBinding.textAddCategory.text = getString(R.string.add_new_expense_category)

        viewBinding.textCategory.addTextChangedListener(categoryTextWatcher)
    }

    private val categoryTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(editable: Editable?) {
            if (isValidCategoryName()) {
                if (!categoryAlreadyExist()) viewBinding.categoryLayout.error = null
                else viewBinding.categoryLayout.error = getString(R.string.category_already_exist)
            } else {
                viewBinding.categoryLayout.error = getString(R.string.field_required)
            }
        }
    }

    private fun getCategoryName(): String = viewBinding.textCategory.text.toString()
    private fun isValidCategoryName(): Boolean = getCategoryName().isNotBlank()
    private fun categoryAlreadyExist(): Boolean = oldList?.contains(getCategoryName()) ?: false

    private fun setOnClickListener() {
        viewBinding.btnAddCategory.setOnClickListener {
            if(isValidCategoryName() && !categoryAlreadyExist()){
                oldList?.add(getCategoryName())
                if (isIncomeCategory) {
                    viewModel.updateUserIncomeCategoryList(IncomeCategoryListEntity(oldList as List<String>))
                } else {
                    viewModel.updateUserExpenseCategoryList(ExpenseCategoryListEntity(oldList as List<String>))
                }
            }
        }
    }

    private fun setUpObserver() {
        viewModel.dataUpdatedResult.observe(viewLifecycleOwner, OneTimeObserver {
            if (it) {
                if (isIncomeCategory) activityViewModel.addCategoryResult(TransactionType.Income)
                else activityViewModel.addCategoryResult(TransactionType.Expense)
                dismiss()
            }
        })
    }

    companion object {
        private const val KEY_LIST = "getList"
        private const val KEY_IS_INCOME = "isIncome"
        fun newInstance(list: List<String>, isIncome: Boolean) = AddCategoryDialogFragment().apply {
            arguments = Bundle().apply {
                putStringArrayList(KEY_LIST, list as ArrayList<String>)
                putBoolean(KEY_IS_INCOME, isIncome)
            }
        }
    }
}