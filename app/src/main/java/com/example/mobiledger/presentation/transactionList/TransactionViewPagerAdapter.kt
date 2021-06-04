package com.example.mobiledger.presentation.transactionList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.databinding.TransactionListRecyclerviewLayoutBinding
import com.example.mobiledger.presentation.home.TransactionData

class TransactionViewPagerAdapter(
    private val context: Context,
    private val incomeList: List<TransactionData>?,
    private val expenseList: List<TransactionData>?
) :
    RecyclerView.Adapter<TransactionViewPagerAdapter.PageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewPagerAdapter.PageHolder {
        return PageHolder(TransactionListRecyclerviewLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: TransactionViewPagerAdapter.PageHolder, position: Int) {
        val linearLayoutManager = LinearLayoutManager(context)
        holder.rv.apply {
            layoutManager = linearLayoutManager
            adapter = holder.transactionAdapter
        }

        when (position) {
            0 -> {
                if (incomeList != null)
                    holder.transactionAdapter.addList(incomeList)
            }
            1 -> {
                if (expenseList != null)
                    holder.transactionAdapter.addList(expenseList)
            }
        }
    }

    override fun getItemCount(): Int = 2

    inner class PageHolder(val viewBinding: TransactionListRecyclerviewLayoutBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        val rv: RecyclerView = viewBinding.rvTransaction

        val transactionAdapter: TransactionAdapter by lazy { TransactionAdapter() }

    }


}