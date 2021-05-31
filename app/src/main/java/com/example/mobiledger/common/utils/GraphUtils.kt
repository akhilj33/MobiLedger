package com.example.mobiledger.common.utils

import android.graphics.Color
import android.graphics.Color.rgb
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import java.text.DecimalFormat


object GraphUtils {
    fun pieChart(pieChart: PieChart, list: List<PieEntry>, showLegend: Boolean) {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(2f, 5f, 2f, 2f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.transparentCircleRadius = 61f
        if (!showLegend) pieChart.legend.isEnabled = false
        val pieDataSet = PieDataSet(list, "")
        pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
//        pieDataSet.sliceSpace = 3f
        pieDataSet.selectionShift = 5f
        pieDataSet.colors = getGraphColorList()
        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(18f)
        pieData.setValueTextColor(Color.BLACK)
        pieData.setValueFormatter(DecimalRemover(DecimalFormat("###,###,###"), pieChart))
        pieChart.data = pieData
        pieChart.setCenterTextSize(30f)
        pieChart.setDrawEntryLabels(false)
        pieChart.setTouchEnabled(false)
        pieChart.animateY(1000, Easing.EaseInOutCubic)
    }

    fun barChart(barChart: BarChart, arrayList: List<BarEntry>, xAxisValues: List<String>) {
        barChart.description.isEnabled = false
        barChart.xAxis.setDrawGridLines(false)
        barChart.setPinchZoom(false)
        barChart.setDrawBarShadow(false)
        barChart.setMaxVisibleValueCount(25)

        barChart.setDrawGridBackground(false)
        val barDataSet = BarDataSet(arrayList, "")
        barDataSet.colors = getGraphColorList()
        val barData = BarData(barDataSet)
        barData.barWidth = 0.2f
        barChart.data = barData
        barChart.setFitBars(true)

        barData.setValueTextSize(10f)

        val xAxis: XAxis = barChart.xAxis
        xAxis.textSize = 13f
        xAxis.isGranularityEnabled = true
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)

        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.isEnabled = true
        barChart.xAxis.setDrawGridLines(false)
        barChart.extraBottomOffset = 4f
        // add a nice and smooth animation
        // add a nice and smooth animation
        barChart.animateY(1500)


        barChart.legend.isEnabled = false

        barChart.axisRight.setDrawLabels(false)
        barChart.axisLeft.setDrawLabels(true)
        barChart.setTouchEnabled(false)
        barChart.isDoubleTapToZoomEnabled = false
        barChart.xAxis.isEnabled = true
        barChart.xAxis.position = XAxisPosition.BOTTOM
        barChart.invalidate()
    }

    fun getGraphColorList() = listOf(
        rgb(255, 82, 82), rgb(130, 177, 255),
        rgb(255, 196, 0), rgb(124, 179, 66),
        rgb(141, 110, 99), rgb(77, 182, 172)
    )

    val otherColor = rgb(161, 136, 127)
}

class DecimalRemover(format: DecimalFormat?, pieChart: PieChart) : PercentFormatter(pieChart) {
    lateinit var mFormat: DecimalFormat
    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value).toString() + " %"
    }

    init {
        this.mFormat = format
    }
}