package com.example.mobiledger.presentation.statsdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.common.utils.GraphUtils
import com.example.mobiledger.databinding.StatsBarchartRowItemBinding
import com.example.mobiledger.databinding.StatsLinechartRowItemBinding

class StatsDetailChildGraphAdapter(private val items: MutableList<StatsDetailGraphViewItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        return when (StatsDetailGraphViewType.values()[viewType]) {
            StatsDetailGraphViewType.BarChartView -> BarGraphViewHolder(StatsBarchartRowItemBinding.inflate(layoutInflater, parent, false))
            StatsDetailGraphViewType.LineChartView -> LineGraphViewHolder(StatsLinechartRowItemBinding.inflate(layoutInflater, parent, false))

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is StatsDetailGraphViewItem.BarGraphRow -> (holder as StatsDetailChildGraphAdapter.BarGraphViewHolder).bind(item)
            is StatsDetailGraphViewItem.LineGraphRow -> (holder as StatsDetailChildGraphAdapter.LineGraphViewHolder).bind(item)

        }
    }

    /*---------------------------------View Holders---------------------------- */

    inner class BarGraphViewHolder(private val viewBinding: StatsBarchartRowItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: StatsDetailGraphViewItem.BarGraphRow) {
            viewBinding.apply {
               val labelList =  item.labelList.map { context.resources.getString(it) }
                GraphUtils.barChart(barChart, item.barEntryList, labelList)
                tvBarChart.text = context.resources.getString(item.header)
            }
        }
    }

    inner class LineGraphViewHolder(private val viewBinding: StatsLinechartRowItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: StatsDetailGraphViewItem.LineGraphRow) {
            viewBinding.apply {
                GraphUtils.lineChart(lineChart, item.entryList, context)
                tvLineChart.text = context.resources.getString(item.header)
            }
        }
    }
}