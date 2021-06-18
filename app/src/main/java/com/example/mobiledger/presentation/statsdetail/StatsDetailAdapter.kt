package com.example.mobiledger.presentation.statsdetail

import CirclePagerIndicatorDecoration
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.R
import com.example.mobiledger.common.utils.DefaultCategoryUtils
import com.example.mobiledger.databinding.StatsChildRecyclerviewItemBinding
import com.example.mobiledger.databinding.StatsDetailTransactionItemBinding
import com.example.mobiledger.databinding.StatsHeaderItemBinding
import com.example.mobiledger.domain.enums.TransactionType
import java.util.*

class StatsDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    private val items: MutableList<StatsDetailViewItem> = mutableListOf()
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType.ordinal
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (StatsDetailViewType.values()[viewType]) {
            StatsDetailViewType.Date -> DateViewHolder(StatsHeaderItemBinding.inflate(layoutInflater, parent, false))
            StatsDetailViewType.GraphData -> GraphsViewHolder(StatsChildRecyclerviewItemBinding.inflate(layoutInflater, parent, false))
            StatsDetailViewType.CategoryData -> TransactionDataViewHolder(
                StatsDetailTransactionItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is StatsDetailViewItem.DateRow -> (holder as StatsDetailAdapter.DateViewHolder).bind(item.date, item.amount)
            is StatsDetailViewItem.GraphDataRow -> (holder as StatsDetailAdapter.GraphsViewHolder).bind(item.data)
            is StatsDetailViewItem.TransactionDataRow -> (holder as StatsDetailAdapter.TransactionDataViewHolder).bind(item.data)
        }
    }

    /*---------------------------------View Holders---------------------------- */

    inner class DateViewHolder(private val viewBinding: StatsHeaderItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(headerString: String, amount: String?) {
            viewBinding.tvHeader.text = headerString
            viewBinding.tvAmount.text = amount
            val transactionType = TransactionType.getTransactionType(headerString.toLowerCase(Locale.getDefault()))
            if (transactionType == TransactionType.Income) viewBinding.tvAmount.setTextColor(
                ContextCompat.getColorStateList(context, R.color.colorGreen)
            )
            else if (transactionType == TransactionType.Expense) viewBinding.tvAmount.setTextColor(
                ContextCompat.getColorStateList(context, R.color.colorDarkRed)
            )
        }
    }

    inner class TransactionDataViewHolder(private val viewBinding: StatsDetailTransactionItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: TransactionData) {
            viewBinding.apply {
                ivCategoryIcon.background =
                    ContextCompat.getDrawable(context, DefaultCategoryUtils.getCategoryIcon(item.transactionEntity.category, item.transactionEntity.transactionType))
                tvTransactionName.text = item.transactionEntity.name
                (item.percent + "%").also { tvPercentage.text = it }
                tvAmount.text = item.transactionEntity.amount.toString()
                tvCategoryName.text = item.transactionEntity.category
            }
        }
    }

    inner class GraphsViewHolder(private val viewBinding: StatsChildRecyclerviewItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(data: MutableList<StatsDetailGraphViewItem>) {
            viewBinding.childGraphRv.apply {
                val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                linearLayoutManager.initialPrefetchItemCount = 3
                layoutManager = linearLayoutManager
                adapter = StatsDetailChildGraphAdapter(data)
                setRecycledViewPool(viewPool)
                val snapHelper = PagerSnapHelper()
                if (this.onFlingListener == null && isCircleIndicatorVisible()) {
                    snapHelper.attachToRecyclerView(this)
                    addItemDecoration(CirclePagerIndicatorDecoration())
                }
            }
        }
    }

    /*---------------------------------Utility Functions---------------------------- */

    fun addItemList(statsItemList: List<StatsDetailViewItem>) {
        items.clear()
        items.addAll(statsItemList)
        notifyDataSetChanged()
    }

    fun isCircleIndicatorVisible(): Boolean{
        if(items[0].viewType == StatsDetailViewType.GraphData && (items[0] as StatsDetailViewItem.GraphDataRow).data.size>1)
            return true
        return false
    }

}