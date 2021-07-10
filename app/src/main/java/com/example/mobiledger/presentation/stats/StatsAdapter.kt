package com.example.mobiledger.presentation.stats

import CirclePagerIndicatorDecoration
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.R
import com.example.mobiledger.common.extention.toAmount
import com.example.mobiledger.databinding.StatsCategoryItemBinding
import com.example.mobiledger.databinding.StatsChildRecyclerviewItemBinding
import com.example.mobiledger.databinding.StatsEmptyItemBinding
import com.example.mobiledger.databinding.StatsHeaderItemBinding
import com.example.mobiledger.domain.enums.TransactionType

class StatsAdapter(private val onCategoryItemClick: (categoryNameList: List<String>, amount: Long) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    private val items: MutableList<StatsViewItem> = mutableListOf()
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType.ordinal
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (StatsViewType.values()[viewType]) {
            StatsViewType.Header -> HeaderViewHolder(StatsHeaderItemBinding.inflate(layoutInflater, parent, false))
            StatsViewType.GraphData -> GraphsViewHolder(StatsChildRecyclerviewItemBinding.inflate(layoutInflater, parent, false))
            StatsViewType.CategoryData -> CategoryDataViewHolder(StatsCategoryItemBinding.inflate(layoutInflater, parent, false))
            StatsViewType.EmptyScreen -> EmptyDataViewHolder(StatsEmptyItemBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is StatsViewItem.HeaderDataRow -> (holder as StatsAdapter.HeaderViewHolder).bind(item.data, item.amount)
            is StatsViewItem.GraphDataRow -> (holder as StatsAdapter.GraphsViewHolder).bind(item.data)
            is StatsViewItem.CategoryDataRow -> (holder as StatsAdapter.CategoryDataViewHolder).bind(item.data)
            is StatsViewItem.EmptyDataRow -> (holder as StatsAdapter.EmptyDataViewHolder).bind()
        }
    }

    /*---------------------------------View Holders---------------------------- */

    inner class HeaderViewHolder(private val viewBinding: StatsHeaderItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(headerString: Int, amount: String) {
            val header = context.resources.getString(headerString)
            viewBinding.tvHeader.text = header
            viewBinding.tvAmount.text = amount
            val transactionType = TransactionType.getTransactionType(header.lowercase())
            if (transactionType == TransactionType.Income) viewBinding.tvAmount.setTextColor(
                ContextCompat.getColorStateList(context, R.color.colorGreen)
            )
            else if (transactionType == TransactionType.Expense) viewBinding.tvAmount.setTextColor(
                ContextCompat.getColorStateList(context, R.color.colorDarkRed)
            )

        }
    }

    inner class CategoryDataViewHolder(private val viewBinding: StatsCategoryItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: CategoryData) {
            viewBinding.apply {
                ivCategoryIcon.background = ContextCompat.getDrawable(context, item.categoryIcon)
                tvCategoryName.text = item.name
                colorIndicator.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item.color))
                (item.percent + "%").also { tvPercentage.text = it }
                tvAmount.text = item.amount.toAmount()

                if (item.categoryType == TransactionType.Income)
                    root.setOnClickListener { onCategoryItemClick(item.categoryList, item.amount) }
                else if (item.categoryType == TransactionType.Expense)
                    root.setOnClickListener { onCategoryItemClick(item.categoryList, 0L - item.amount) }
            }
        }
    }

    inner class GraphsViewHolder(private val viewBinding: StatsChildRecyclerviewItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(data: MutableList<StatsGraphViewItem>) {
            viewBinding.childGraphRv.apply {
                val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                linearLayoutManager.initialPrefetchItemCount = 3
                layoutManager = linearLayoutManager
                adapter = StatsChildGraphAdapter(data)
                (adapter as StatsChildGraphAdapter).stateRestorationPolicy = StateRestorationPolicy.PREVENT
                setRecycledViewPool(viewPool)
                val snapHelper = PagerSnapHelper()
                if (this.onFlingListener == null) {
                    snapHelper.attachToRecyclerView(this)
                    addItemDecoration(CirclePagerIndicatorDecoration())
                }
            }
        }
    }

    inner class EmptyDataViewHolder(private val viewBinding: StatsEmptyItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind() {
            viewBinding.apply {
                animationView.playAnimation()
            }
        }
    }

    /*---------------------------------Utility Functions---------------------------- */

    fun addItemList(statsItemList: List<StatsViewItem>) {
        items.clear()
        items.addAll(statsItemList)
        notifyDataSetChanged()
    }

}