package com.example.mobiledger.presentation.categoryFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.setWidthPercent
import com.example.mobiledger.databinding.DialogFragmentAddCategoryBinding
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setWidthPercent(85)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setUpObserver()
    }

    private fun setOnClickListener() {
        viewBinding.btnAddCategory.setOnClickListener {
            val categoryName = viewBinding.textCategory.text.toString()
            if (categoryName.isNotEmpty() || categoryName.isNotBlank()) {
                oldList?.add(categoryName)
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