package com.example.mobiledger.presentation.stats

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.mobiledger.domain.enums.TransactionType
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry

sealed class StatsViewItem(val viewType: StatsViewType) {
    data class HeaderDataRow(@StringRes val data: Int, val amount: String, val type: StatsViewType = StatsViewType.Header) : StatsViewItem(type)
    data class GraphDataRow(val data: MutableList<StatsGraphViewItem>, val type: StatsViewType = StatsViewType.GraphData) :
        StatsViewItem(type)

    data class CategoryDataRow(val data: CategoryData, val type: StatsViewType = StatsViewType.CategoryData) :
        StatsViewItem(type)
}

data class CategoryData(
    val name: String,
    @DrawableRes val categoryIcon: Int,
    val categoryType: TransactionType,
    val percent: String,
    val color: String,
    val amount: Long,
)

sealed class StatsGraphViewItem(val viewType: StatsGraphViewType) {
    data class PieGraphRow(val pieEntryList: List<PieEntry>,@StringRes val header: Int, val type: StatsGraphViewType = StatsGraphViewType.PieChartView) :
        StatsGraphViewItem(type)

    data class BarGraphRow(val barEntryList: List<BarEntry>,val labelList: List<Int>, @StringRes val header: Int, val type: StatsGraphViewType = StatsGraphViewType.BarChartView) :
        StatsGraphViewItem(type)
}

enum class StatsViewType { Header, GraphData, CategoryData }
enum class StatsGraphViewType { PieChartView, BarChartView }


