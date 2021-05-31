package com.example.mobiledger.presentation.stats

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.common.utils.GraphUtils
import com.example.mobiledger.databinding.StatsBarchartRowItemBinding
import com.example.mobiledger.databinding.StatsPiechartRowItemBinding

class StatsChildGraphAdapter(private val items: MutableList<StatsGraphViewItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType.ordinal
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (StatsGraphViewType.values()[viewType]) {
            StatsGraphViewType.PieChartView -> PieChartViewHolder(StatsPiechartRowItemBinding.inflate(layoutInflater, parent, false))
            StatsGraphViewType.BarChartView -> BarGraphViewHolder(StatsBarchartRowItemBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is StatsGraphViewItem.PieGraphRow -> (holder as StatsChildGraphAdapter.PieChartViewHolder).bind(item)
            is StatsGraphViewItem.BarGraphRow -> (holder as StatsChildGraphAdapter.BarGraphViewHolder).bind(item)
        }
    }

    /*---------------------------------View Holders---------------------------- */

    inner class PieChartViewHolder(private val viewBinding: StatsPiechartRowItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: StatsGraphViewItem.PieGraphRow) {
            viewBinding.apply {
                GraphUtils.pieChart(pieChart, item.pieEntryList, false)
                tvPieChart.text = context.resources.getString(item.header)
            }
        }
    }

    inner class BarGraphViewHolder(private val viewBinding: StatsBarchartRowItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: StatsGraphViewItem.BarGraphRow) {
            viewBinding.apply {
               val labelList =  item.labelList.map { context.resources.getString(it) }
                GraphUtils.barChart(barChart, item.barEntryList, labelList)
                tvBarChart.text = context.resources.getString(item.header)
            }
        }
    }

    /*---------------------------------Utility Functions---------------------------- */

    fun addItemList(statsGraphItemList: List<StatsGraphViewItem>) {
        items.clear()
        items.addAll(statsGraphItemList)
        notifyDataSetChanged()
    }
}