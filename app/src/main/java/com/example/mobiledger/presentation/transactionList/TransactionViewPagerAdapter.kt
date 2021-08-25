package com.example.mobiledger.presentation.transactionList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.R
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.databinding.TransactionListRecyclerviewLayoutBinding
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.presentation.home.TransactionData

class TransactionViewPagerAdapter(
    val onTransactionItemClick: (TransactionEntity) -> Unit
) : RecyclerView.Adapter<TransactionViewPagerAdapter.PageHolder>() {

    private lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    private val incomeList: MutableList<TransactionData> = mutableListOf()
    private val expenseList: MutableList<TransactionData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewPagerAdapter.PageHolder {
        return PageHolder(TransactionListRecyclerviewLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: TransactionViewPagerAdapter.PageHolder, position: Int) {
        holder.bind()
        when (position) {
            0 -> {
                if (incomeList.isNotEmpty()) {
                    transactionAdapter.addList(incomeList)
                    holder.emptyTransactionScreen.gone()
                    holder.emptyTransactionListText.gone()
                } else {
                    holder.emptyTransactionScreen.visible()
                    holder.emptyTransactionScreen.playAnimation()
                    holder.emptyTransactionListText.visible()
                    holder.emptyTransactionListText.text = context.getString(R.string.no_income_for_this_month)
                }
            }
            1 -> {
                if (expenseList.isNotEmpty()) {
                    transactionAdapter.addList(expenseList)
                } else {
                    holder.emptyTransactionScreen.visible()
                    holder.emptyTransactionScreen.playAnimation()
                    holder.emptyTransactionListText.visible()
                    holder.emptyTransactionListText.text = context.getString(R.string.no_expense_for_this_month)
                }
            }
        }
    }

    override fun getItemCount(): Int = 2

    lateinit var transactionAdapter: TransactionAdapter

    inner class PageHolder(val viewBinding: TransactionListRecyclerviewLayoutBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        val emptyTransactionScreen = viewBinding.animationView
        val emptyTransactionListText = viewBinding.textEmptyList
        fun bind() {
            transactionAdapter = TransactionAdapter(onTransactionItemClick)
            viewBinding.rvTransaction.apply {
                val linearLayoutManager = LinearLayoutManager(context)
                layoutManager = linearLayoutManager
                adapter = transactionAdapter
            }

        }

    }

    /*---------------------------------Utility Functions---------------------------- */

    fun addItemList(newIncomeList: List<TransactionData>, newExpenseList: List<TransactionData>) {
        incomeList.clear()
        expenseList.clear()
        incomeList.addAll(newIncomeList)
        expenseList.addAll(newExpenseList)
        notifyDataSetChanged()
    }

    fun deleteItem(transactionEntity: TransactionEntity) {
        transactionAdapter.deleteItem(transactionEntity)
    }

    fun updateItem(transactionEntity: TransactionEntity) {
        transactionAdapter.updateItem(transactionEntity)
    }

}