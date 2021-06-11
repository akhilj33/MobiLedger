package com.example.mobiledger.common.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.Typeface
import android.view.LayoutInflater
import com.example.mobiledger.R
import com.example.mobiledger.databinding.MarkerLayoutBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
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
        pieDataSet.colors = getGraphColorList()
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

    //todo
    // 1 - marker not working
    // 2 - Padding not adding between first item and white axis
    // 3 - Remove grids
    fun lineChart(lineChart: LineChart, arrayList: List<Entry>, context: Context) {
        val dataSet = LineDataSet(arrayList, "Unused label")
        dataSet.apply {
            this.color = Color.BLACK
            this.valueTextColor = Color.GRAY
            this.highLightColor = Color.RED
            this.setCircleColor(Color.BLACK)
            this.circleHoleColor = Color.BLACK
            this.setDrawValues(false)
            this.lineWidth = 1.5f
            this.isHighlightEnabled = true
            this.setDrawHighlightIndicators(false)
        }

        with(lineChart) {
            // (1)
            axisLeft.isEnabled = true
            axisRight.isEnabled = false
            xAxis.isEnabled = true
            xAxis.position = XAxisPosition.BOTTOM
            xAxis.labelRotationAngle = 45f
            xAxis.labelCount = 10
            xAxis.spaceMin = 0.1f
            xAxis.spaceMax = 0.1f
            xAxis.setAvoidFirstLastClipping(true)
            legend.isEnabled = false
            description.isEnabled = false

            // (2)
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)

            xAxis.valueFormatter = LineChartXAxisValueFormatter()
        }

        lineChart.marker = MyMarker(context)
        lineChart.data = LineData(dataSet)
        lineChart.invalidate()
    }

    fun getGraphColorList() = listOf(
        rgb(124, 179, 66), rgb(255, 82, 82),
        rgb(130, 177, 255), rgb(255, 196, 0),
        rgb(141, 110, 99), rgb(77, 182, 172)
    )

    val otherColor = rgb(141, 110, 99)
}

class MyMarker(context: Context) : MarkerView(context, R.layout.marker_layout) {

    override fun refreshContent(entry: Entry, highlight: Highlight) {
        val viewBinding = MarkerLayoutBinding.inflate(LayoutInflater.from(context))
        viewBinding.monthName.text = entry.x.toString()
        viewBinding.tvAmount.text = entry.y.toString()
        super.refreshContent(entry, highlight)
    }
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