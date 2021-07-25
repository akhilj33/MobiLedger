package com.example.mobiledger.presentation.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.mobiledger.R
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.common.extention.toAmount
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.common.utils.GraphUtils
import com.example.mobiledger.databinding.*
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.github.mikephil.charting.data.PieEntry

class HomeAdapter(
    val onDeleteItemClick: (TransactionEntity, Int) -> Unit,
    val onTransactionItemClick: (TransactionEntity) -> Unit,
    val onAllTransactionClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private val viewBinderHelper = ViewBinderHelper()

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
            HomeViewType.MonthlyTotalPieChart -> MonthlyPieViewHolder(HomeMonthlyPieItemBinding.inflate(layoutInflater, parent, false))
            HomeViewType.TransactionData -> TransactionDataViewHolder(HomeTransactionItemBinding.inflate(layoutInflater, parent, false))
            HomeViewType.EmptyData -> EmptyDataViewHolder(HomeEmptyItemBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HomeViewItem.HeaderDataRow -> (holder as HeaderViewHolder).bind(item.headerData)
            is HomeViewItem.MonthlyDataRow -> (holder as MonthlyDataViewHolder).bind(item.data)
            is HomeViewItem.MonthlyTotalPie -> (holder as MonthlyPieViewHolder).bind(item.pieEntryList)
            is HomeViewItem.TransactionDataRow -> (holder as TransactionDataViewHolder).bind(item.data)
            is HomeViewItem.EmptyDataRow -> (holder as EmptyDataViewHolder).bind()
        }
    }

    /*---------------------------------View Holders---------------------------- */

    inner class HeaderViewHolder(private val viewBinding: HomeHeaderItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(headerData: HeaderData) {
            viewBinding.apply {
                tvViewAll.setOnSafeClickListener { onAllTransactionClicked() }
                tvHeader.text = context.resources.getString(headerData.headerString)
                if (headerData.isSecondaryHeaderVisible) tvViewAll.visible()
                else tvViewAll.gone()
            }
        }
    }

    inner class MonthlyDataViewHolder(private val viewBinding: HomeMonthlyDataItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: MonthlyData) {
            viewBinding.apply {
                tvIncomeAmount.text = item.incomeAmount.toAmount()
                tvExpenseAmount.text = item.expenseAmount.toAmount()
                tvSavingAmount.text = (item.incomeAmount-item.expenseAmount).toAmount()
            }
        }
    }

    inner class MonthlyPieViewHolder(private val viewBinding: HomeMonthlyPieItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: ArrayList<PieEntry>) {
            viewBinding.apply {
                GraphUtils.pieChart(pieChart, item, true)
            }
        }
    }

    inner class EmptyDataViewHolder(private val viewBinding: HomeEmptyItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind() {
            viewBinding.apply {
                animationView.playAnimation()
            }
        }
    }

    inner class TransactionDataViewHolder(private val viewBinding: HomeTransactionItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(data: TransactionData) {
            val item = data.transactionEntity
            viewBinding.apply {
                deleteSwipeAction.setOnSafeClickListener { onDeleteItemClick(item, adapterPosition) }
                transactionRoot.setOnSafeClickListener {onTransactionItemClick(item)}
                viewBinderHelper.setOpenOnlyOne(true)
                viewBinderHelper.bind(swipelayout, item.id)
                viewBinderHelper.closeLayout(item.id)
                tvTransactionName.text = item.name
                tvAmount.text = item.amount.toAmount()
                tvCategory.text = item.category
                ivCategoryIcon.background = ContextCompat.getDrawable(context, data.categoryIcon)
                tvTime.text = DateUtils.getDateInDDMMMyyyyFormat(item.transactionTime)

                if (item.transactionType == TransactionType.Income) tvAmount.setTextColor(
                    ContextCompat.getColorStateList(context, R.color.colorGreen)
                )
                else if (item.transactionType == TransactionType.Expense) tvAmount.setTextColor(
                    ContextCompat.getColorStateList(context, R.color.colorDarkRed)
                )
            }
        }
    }

    /*---------------------------------Utility Functions---------------------------- */

    fun addItemList(homeItemList: List<HomeViewItem>) {
        items.clear()
        items.addAll(homeItemList)
        notifyDataSetChanged()
    }

    fun insertHomeViewItem(homeItem: HomeViewItem) {
        val newIndex = items.size
        if (items.add(homeItem)) notifyItemInserted(newIndex)
    }

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun closeItemLayout(itemId: String) {
        viewBinderHelper.closeLayout(itemId)
    }
}