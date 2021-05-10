package com.example.mobiledger.presentation.categoryFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.databinding.FragmentExpenseCategoryBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.categoryFragment.adapter.CategoryAdapter
import com.example.mobiledger.presentation.recordtransaction.AddTransactionDialogFragmentViewModel

class ExpenseCategoryFragment : BaseFragment<FragmentExpenseCategoryBinding, BaseNavigator>(R.layout.fragment_expense_category) {

    private val viewModel: ExpenseCategoryViewModel by viewModels { viewModelFactory }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeExpenseErrorView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        viewModel.getExpenseCategoryList()
    }

    private fun setUpObserver() {
        viewModel.expenseCategoryList.observe(viewLifecycleOwner, OneTimeObserver {
            initRecyclerView(it.expenseCategoryList)
        })

        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewBinding.expenseCategoryProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.expenseCategoryProgressBar.visibility = View.GONE
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                AddTransactionDialogFragmentViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })
    }

    private fun initRecyclerView(incomeCategoryList: List<String>) {
        val expenseCategoryAdapter = CategoryAdapter(incomeCategoryList)
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvExpenseCategory.apply {
            layoutManager = linearLayoutManager
            adapter = expenseCategoryAdapter
        }
    }


    companion object {
        fun newInstance() = ExpenseCategoryFragment()
    }
}