package com.example.mobiledger.presentation.budget

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.mobiledger.databinding.BudgetCategoryItemBinding
import com.example.mobiledger.databinding.BudgetEmptyItemBinding
import com.example.mobiledger.databinding.BudgetHeaderLayoutBinding
import com.example.mobiledger.databinding.MonthlyBudgetOverviewItemBinding

class BudgetAdapter(
    val onMakeBudgetClick: () -> Unit,
    val onBudgetOverViewClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    private val viewBinderHelper = ViewBinderHelper()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    private val items: MutableList<BudgetViewItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (BudgetViewType.values()[viewType]) {
            BudgetViewType.Header -> BudgetHeaderViewHolder(BudgetHeaderLayoutBinding.inflate(layoutInflater, parent, false))
            BudgetViewType.MonthlyBudgetOverview -> MonthlyBudgetOverviewViewHolder(
                MonthlyBudgetOverviewItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            BudgetViewType.BudgetData -> BudgetDataViewHolder(
                BudgetCategoryItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )

            BudgetViewType.EmptyBudget -> BudgetEmptyViewHolder(
                BudgetEmptyItemBinding.inflate(
                    layoutInflater, parent, false
                )
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is BudgetViewItem.BudgetHeaderData -> (holder as BudgetAdapter.BudgetHeaderViewHolder).bind(item.data)
            is BudgetViewItem.BudgetOverviewData -> (holder as BudgetAdapter.MonthlyBudgetOverviewViewHolder).bind(item.data)
            is BudgetViewItem.BudgetCategory -> (holder as BudgetAdapter.BudgetDataViewHolder).bind(item.data)
            is BudgetViewItem.BudgetEmpty -> (holder as BudgetAdapter.BudgetEmptyViewHolder).bind()
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType.ordinal
    }


    /*---------------------------------View Holders---------------------------- */

    inner class BudgetHeaderViewHolder(private val viewBinding: BudgetHeaderLayoutBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(headerString: Int) {
            viewBinding.tvHeader.text = context.resources.getString(headerString)
        }
    }

    inner class MonthlyBudgetOverviewViewHolder(private val viewBinding: MonthlyBudgetOverviewItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: MonthlyBudgetOverviewData) {
            viewBinding.apply {
                rootBudgetOverview.setOnClickListener {
                    onBudgetOverViewClick()
                }
                tvMaxBudgetAmount.text = item.maxBudget
                tvTotalBudgetAmount.text = item.totalBudget
            }
        }
    }

    inner class BudgetDataViewHolder(private val viewBinding: BudgetCategoryItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: BudgetCategoryData) {
            viewBinding.apply {

            }
        }
    }

    inner class BudgetEmptyViewHolder(private val viewBinding: BudgetEmptyItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind() {
            viewBinding.apply {
                btnMakeBudget.setOnClickListener {
                    onMakeBudgetClick()
                }
            }
        }
    }


    /*---------------------------------Utility Functions---------------------------- */

    fun addItemList(budgetItemList: List<BudgetViewItem>) {
        items.clear()
        items.addAll(budgetItemList)
        notifyDataSetChanged()
    }
}
