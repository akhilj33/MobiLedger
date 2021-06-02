package com.example.mobiledger.presentation.statsdetail

import androidx.annotation.StringRes
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry

sealed class StatsDetailViewItem(val viewType: StatsDetailViewType) {
    data class DateRow(val date: String, val amount: String?, val type: StatsDetailViewType = StatsDetailViewType.Date) :
        StatsDetailViewItem(type)

    data class GraphDataRow(val data: MutableList<StatsDetailGraphViewItem>, val type: StatsDetailViewType = StatsDetailViewType.GraphData) :
        StatsDetailViewItem(type)

    data class TransactionDataRow(val data: TransactionData, val type: StatsDetailViewType = StatsDetailViewType.CategoryData) :
        StatsDetailViewItem(type)
}

data class TransactionData(val transactionEntity: TransactionEntity, val percent:String)

sealed class StatsDetailGraphViewItem(val viewType: StatsDetailGraphViewType) {
    data class BarGraphRow(val barEntryList: List<BarEntry>, val labelList: List<Int>, @StringRes val header: Int,
        val type: StatsDetailGraphViewType = StatsDetailGraphViewType.BarChartView) : StatsDetailGraphViewItem(type)

    data class LineGraphRow(val entryList: List<Entry>, @StringRes val header: Int,
                            val type: StatsDetailGraphViewType = StatsDetailGraphViewType.LineChartView) : StatsDetailGraphViewItem(type)
}

enum class StatsDetailViewType { Date, GraphData, CategoryData }
enum class StatsDetailGraphViewType { BarChartView, LineChartView }


