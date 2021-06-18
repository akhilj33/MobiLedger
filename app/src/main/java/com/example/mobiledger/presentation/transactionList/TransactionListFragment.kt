package com.example.mobiledger.presentation.transactionList

import android.os.Bundle
import android.view.View
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.transformer.ZoomOutPageTransformer
import com.example.mobiledger.databinding.FragmentTransactionListScreenBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.home.TransactionData
import com.google.android.material.tabs.TabLayoutMediator
import okhttp3.internal.filterList


class TransactionListFragment :
    BaseFragment<FragmentTransactionListScreenBinding, BaseNavigator>(R.layout.fragment_transaction_list_screen) {


    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView
    private var transactionList: ArrayList<TransactionData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionList?.clear()
            transactionList = it.getSerializable(KEY_LIST) as ArrayList<TransactionData>
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    private fun initViewPager() {
        viewBinding.pager.isUserInputEnabled = true
        viewBinding.pager.setPageTransformer(ZoomOutPageTransformer())
        val incomeList =
            transactionList?.filterList { transactionType == TransactionType.Income }
        val expenseList =
            transactionList?.filterList { transactionType == TransactionType.Expense }

        viewBinding.pager.adapter =
            TransactionViewPagerAdapter(
                requireContext(),
                incomeList,
                expenseList
            )
        TabLayoutMediator(
            viewBinding.tabLayout,
            viewBinding.pager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.income)
                1 -> tab.text = getString(R.string.expense)
            }
        }.attach()
    }

    companion object {
        private const val KEY_LIST = "getList"
        fun newInstance(list: ArrayList<TransactionData>) = TransactionListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(KEY_LIST, list)
            }
        }
    }
}
