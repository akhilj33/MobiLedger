package com.example.mobiledger.common.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.DashPathEffect
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.example.mobiledger.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


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
        pieDataSet.sliceSpace = 1f
        pieDataSet.selectionShift = 5f
        pieDataSet.colors = getPieGraphColorList()
        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(18f)
        pieData.setValueTextColor(Color.BLACK)
        pieData.setValueFormatter(DecimalRemover(DecimalFormat("###,###,###"), pieChart))
        pieChart.data = pieData
        pieChart.setCenterTextSize(14f)
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        pieChart.setDrawEntryLabels(false)
        pieChart.setTouchEnabled(false)
        pieChart.animateY(1000, Easing.EaseInOutCubic)
    }

    fun barChart(barChart: BarChart, arrayList: List<BarEntry>, xAxisValues: List<String>) {
        barChart.description.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setDrawBarShadow(false)
        barChart.setMaxVisibleValueCount(25)

        barChart.setDrawGridBackground(false)
        val barDataSet = BarDataSet(arrayList, "")
        barDataSet.colors = getBarGraphColorList()
        val barData = BarData(barDataSet)
        barData.barWidth = 0.2f
        barChart.data = barData
        barChart.setFitBars(true)

        barData.setValueTextSize(10f)

        val xAxis: XAxis = barChart.xAxis
        xAxis.textSize = 13f
        xAxis.isGranularityEnabled = true
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
        xAxis.setDrawAxisLine(true)

        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.isEnabled = true
        barChart.axisLeft.setDrawLabels(false)
        barChart.xAxis.setDrawGridLines(false)
        barChart.extraBottomOffset = 4f
        // add a nice and smooth animation
        barChart.animateY(1500)

        barChart.legend.isEnabled = false

        if (checkToShowLimitLine(xAxisValues, arrayList)) {
            val line = LimitLine(0f)
            line.lineWidth = 0.5f
            line.enableDashedLine(30f, 10f, 0f)
            line.lineColor = R.color.colorAppBlue
            barChart.axisLeft.addLimitLine(line)
        }

        barChart.axisRight.setDrawLabels(false)
        barChart.axisLeft.setDrawLabels(true)
        barChart.setTouchEnabled(false)
        barChart.isDoubleTapToZoomEnabled = false
        barChart.xAxis.isEnabled = true
        barChart.xAxis.position = XAxisPosition.BOTTOM
        barChart.invalidate()
    }

    fun lineChart(lineChart: LineChart, arrayList: List<Entry>, context: Context) {
        with(lineChart) {
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false

            xAxis.apply {
                enableGridDashedLine(10f, 10f, 0f)
                axisMaximum = arrayList.last().x + 86400
                axisMinimum = arrayList[0].x - 86400
                labelCount = arrayList.size
                position = XAxisPosition.BOTTOM
                labelRotationAngle = 45f
                valueFormatter = LineChartXAxisValueFormatter()
            }

            axisLeft.apply {
                axisMaximum = ((arrayList.maxByOrNull { it.y })?.y ?: 100000f) * 1.5f
                axisMinimum = 0f
                enableGridDashedLine(10f, 10f, 0f)
                setDrawZeroLine(false)
                setDrawLimitLinesBehindData(false)
            }
        }

        val lineDataSet = LineDataSet(arrayList, "")
        lineDataSet.apply {
            setDrawIcons(false)
            enableDashedLine(10f, 5f, 0f)
            enableDashedHighlightLine(10f, 5f, 0f)
            color = Color.DKGRAY
            setCircleColor(Color.DKGRAY)
            lineWidth = 1f
            circleRadius = 3f
            setDrawCircleHole(false)
            valueTextSize = 10f
            setDrawFilled(true)
            formLineWidth = 1f
            formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            formSize = 15f
            val drawable = ContextCompat.getDrawable(context, R.drawable.fade_blue)
            fillDrawable = drawable
        }

        val data = LineData(lineDataSet)
        lineChart.data = data

    }

    fun getPieGraphColorList() = listOf(
        rgb(51, 204, 102), rgb(255, 82, 82),
        rgb(130, 177, 255), rgb(255, 196, 0),
        rgb(141, 110, 99), rgb(77, 182, 172)
    )

    fun getBarGraphColorList() = listOf(
        rgb(176,0,32), rgb(48,0,156), rgb(244,67,54), rgb(0,196,180),
        rgb(240,98,146)
    )

    val otherColor = rgb(141, 110, 99)
}

class LineChartXAxisValueFormatter : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {

        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
        val emissionsMilliSince1970Time = value.toLong() * 1000

        // Show time in local version
        val timeMilliseconds = Date(emissionsMilliSince1970Time)
        val dateTimeFormat = SimpleDateFormat("dd-MMM", Locale.ENGLISH)
        return dateTimeFormat.format(timeMilliseconds)
    }
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

private fun checkToShowLimitLine(xAxisValues: List<String>, arrayList: List<BarEntry>): Boolean {
    val index = xAxisValues.indexOf("Saving")
    if (index != -1) {
        return arrayList[index].y < 0f
    }
    return false
}