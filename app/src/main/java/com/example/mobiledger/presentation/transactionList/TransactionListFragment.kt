package com.example.mobiledger.presentation.transactionList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.common.utils.DefaultCategoryUtils
import com.example.mobiledger.common.utils.JsonUtils.convertJsonStringToObject
import com.example.mobiledger.common.utils.showTransactionDetailDialogFragment
import com.example.mobiledger.databinding.FragmentTransactionListScreenBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionDetailScreenSource
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.home.TransactionData
import com.google.android.material.tabs.TabLayoutMediator
import okhttp3.internal.filterList
import kotlin.math.exp


class TransactionListFragment :
    BaseFragment<FragmentTransactionListScreenBinding, BaseNavigator>(R.layout.fragment_transaction_list_screen, StatusBarColor.BLUE) {

    private val transactionListAdapter: TransactionViewPagerAdapter by lazy { TransactionViewPagerAdapter(onTransactionItemClick).apply {
        addItemList(incomeList, expenseList)
    } }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView
    private var transactionList: ArrayList<TransactionData>? = null
    private var incomeList: MutableList<TransactionData> = mutableListOf()
    private var expenseList: MutableList<TransactionData> = mutableListOf()

    private lateinit var monthYear: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionList?.clear()
            transactionList = it.getSerializable(KEY_LIST) as ArrayList<TransactionData>
            monthYear = it.getString(KEY_MONTH_YEAR) as String
        }
    }

    override fun isBottomNavVisible(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.title.text = getString(R.string.transaction, monthYear)
        initList()
        initViewPager()
        setOnClickListener()
        fragmentResultListener()
    }

    private fun initList() {
        transactionList?.let {
            incomeList =
                it.filterList { transactionType == TransactionType.Income }.toMutableList()
            expenseList =
                it.filterList { transactionType == TransactionType.Expense }.toMutableList()
        }
    }

    private fun setOnClickListener() {
        viewBinding.btnBack.setOnSafeClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initViewPager() {
        viewBinding.pager.isUserInputEnabled = true
//        viewBinding.pager.setPageTransformer(ZoomOutPageTransformer())

        viewBinding.pager.adapter = transactionListAdapter
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

    private val onTransactionItemClick = fun(transactionEntity: TransactionEntity) {
        showTransactionDetailDialogFragment(transactionEntity, requireActivity().supportFragmentManager, TransactionDetailScreenSource.TransactionList)
    }

    private fun fragmentResultListener() {
        setFragmentResultListener(ConstantUtils.TRANSACTION_DETAIL_REQUEST_KEY) { requestKey, bundle ->
            if (requestKey == ConstantUtils.TRANSACTION_DETAIL_REQUEST_KEY) {
                val transactionEntityString = bundle.getString(ConstantUtils.TD_ENTITY_BUNDLE_KEY)
                val transactionEntity = convertJsonStringToObject<TransactionEntity>(transactionEntityString)
                val isTransactionRemoved = bundle.getBoolean(ConstantUtils.TD_IS_DELETE_BUNDLE_KEY)

                if (isTransactionRemoved){
                    transactionEntity?.let{deleteItem(it)}
                }
                else transactionEntity?.let{updateItem(it)}
            }
        }
    }

    fun deleteItem(transactionEntity: TransactionEntity) {
        if (transactionEntity.transactionType == TransactionType.Income){
            val index = incomeList.indexOfFirst { it.id == transactionEntity.id }
            if(index != -1) {
                incomeList.removeAt(index)
            }
        } else {
            val index = expenseList.indexOfFirst { it.id == transactionEntity.id }
            if(index != -1) {
                expenseList.removeAt(index)
            }
        }
        transactionListAdapter.addItemList(incomeList, expenseList)
    }

    fun updateItem(transactionEntity: TransactionEntity) {
        val transactionData = with(transactionEntity){
            TransactionData(id, name, amount.toString(), transactionType, category, this,
                DefaultCategoryUtils.getCategoryIcon(category, transactionType)
            )
        }
        if (transactionEntity.transactionType == TransactionType.Income){
            val index = incomeList.indexOfFirst { it.id == transactionEntity.id }
            if(index != -1) {
                incomeList[index] = transactionData
            }
        } else {
            val index = expenseList.indexOfFirst { it.id == transactionEntity.id }
            if(index != -1) {
                expenseList[index] = transactionData
            }
        }
        transactionListAdapter.addItemList(incomeList, expenseList)
    }

    companion object {
        private const val KEY_LIST = "getList"
        private const val KEY_MONTH_YEAR = "monthYear"
        fun newInstance(list: ArrayList<TransactionData>, monthYear: String) = TransactionListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(KEY_LIST, list)
                putString(KEY_MONTH_YEAR, monthYear)
            }
        }
    }
}
