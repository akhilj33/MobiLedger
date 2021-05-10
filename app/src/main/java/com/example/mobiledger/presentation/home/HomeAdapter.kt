package com.example.mobiledger.presentation.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.R
import com.example.mobiledger.databinding.HomeHeaderItemBinding
import com.example.mobiledger.databinding.HomeMonthlyDataItemBinding
import com.example.mobiledger.databinding.HomeTransactionItemBinding
import com.example.mobiledger.domain.enums.TransactionType

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    private val items: MutableList<HomeViewItem> = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType.ordinal
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (HomeViewType.values()[viewType]) {
            HomeViewType.Header -> HeaderViewHolder(HomeHeaderItemBinding.inflate(layoutInflater, parent, false))
            HomeViewType.MonthlyData -> MonthlyDataViewHolder(HomeMonthlyDataItemBinding.inflate(layoutInflater, parent, false))
            HomeViewType.TransactionData -> TransactionDataViewHolder(HomeTransactionItemBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HomeViewItem.HeaderDataRow -> (holder as HeaderViewHolder).bind(item.data)
            is HomeViewItem.MonthlyDataRow -> (holder as MonthlyDataViewHolder).bind(item.data)
            is HomeViewItem.TransactionDataRow -> (holder as TransactionDataViewHolder).bind(item.data)
        }
    }

    /*---------------------------------View Holders---------------------------- */

    inner class HeaderViewHolder(private val viewBinding: HomeHeaderItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(headerString: Int) {
            viewBinding.tvHeader.text = context.resources.getString(headerString)
        }
    }

    inner class MonthlyDataViewHolder(private val viewBinding: HomeMonthlyDataItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: MonthlyData) {
            viewBinding.apply {
                tvIncomeAmount.text = item.incomeAmount
                tvExpenseAmount.text = item.incomeAmount
            }
        }
    }

    inner class TransactionDataViewHolder(private val viewBinding: HomeTransactionItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: TransactionData) {
            viewBinding.apply {
                tvCategoryName.text = item.name
                tvAmount.text = item.amount
                tvCategory.text = item.category
                ivCategoryIcon.background = ContextCompat.getDrawable(context, item.categoryIcon)
                if (item.transactionType == TransactionType.Income) tvAmount.setTextColor(
                    ContextCompat.getColorStateList(
                        context,
                        R.color.colorGreen
                    )
                )
                else if (item.transactionType == TransactionType.Expense) tvAmount.setTextColor(
                    ContextCompat.getColorStateList(
                        context,
                        R.color.colorDarkRed
                    )
                )
            }
        }
    }

    /*---------------------------------Utility Functions---------------------------- */

    fun addItemList(homeItemList: List<HomeViewItem>) {
        if (homeItemList.isNotEmpty()) {
            val newIndex = items.size
            val newItemsCount = homeItemList.size
            if (items.addAll(homeItemList)) notifyItemRangeInserted(newIndex, newItemsCount)
        }
    }

    fun insertHomeViewItem(homeItem: HomeViewItem) {
        val newIndex = items.size
        if (items.add(homeItem)) notifyItemInserted(newIndex)
    }

}