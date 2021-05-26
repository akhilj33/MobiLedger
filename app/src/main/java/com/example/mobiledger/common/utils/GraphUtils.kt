package com.example.mobiledger.common.utils

import android.graphics.Color
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

object GraphUtils {
    fun pieChart(pieChart: PieChart, arrayList: ArrayList<PieEntry>) {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(2f, 5f, 2f, 2f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.transparentCircleRadius = 61f
        val pieDataSet = PieDataSet(arrayList, "")
        pieDataSet.sliceSpace = 3f
        pieDataSet.selectionShift = 5f
        val colors = intArrayOf(Color.rgb(14, 161, 41), Color.rgb(209, 41, 19))
        val arrayList1 = ArrayList<Int>()
        for (c in colors) {
            arrayList1.add(c)
        }
        pieDataSet.colors = arrayList1
        pieDataSet.colors = ColorTemplate.createColors(colors)
        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(18f)
        pieData.setValueTextColor(Color.WHITE)
        pieChart.data = pieData
        pieChart.setCenterTextSize(30f)
        pieChart.setDrawEntryLabels(false)
        pieChart.animateY(1000, Easing.EaseInOutCubic)
    }

    fun barChart(barChart: BarChart, arrayList: java.util.ArrayList<BarEntry?>?, xAxisValues: java.util.ArrayList<String?>?) {
        barChart.setDrawBarShadow(false)
        barChart.setFitBars(true)
        barChart.setDrawValueAboveBar(true)
        barChart.setMaxVisibleValueCount(25)
        barChart.setPinchZoom(true)
        barChart.setDrawGridBackground(true)
        val barDataSet = BarDataSet(arrayList, "Values")
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        val barData = BarData(barDataSet)
        barData.barWidth = 0.9f
        barData.setValueTextSize(0f)
        barChart.setBackgroundColor(Color.TRANSPARENT) //set whatever color you prefer
        barChart.setDrawGridBackground(false)
        val l = barChart.legend
        l.textSize = 0f
        l.formSize = 0f
        val xAxis = barChart.xAxis
        xAxis.textSize = 13f
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
        xAxis.setDrawGridLines(false)
        barChart.data = barData
    }
}