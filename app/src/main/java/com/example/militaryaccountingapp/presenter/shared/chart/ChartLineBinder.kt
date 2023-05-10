package com.example.militaryaccountingapp.presenter.shared.chart

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.example.militaryaccountingapp.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import timber.log.Timber

class ChartLineBinder(
    private val context: Context,
    private val chart: LineChart,
    private val onChartValueSelectedListener: OnChartValueSelectedListener
) {

    private val onSurface by lazy {
        ContextCompat.getColor(context, R.color.md_onBackground)
    }

    private val surface by lazy {
        ContextCompat.getColor(context, R.color.md_surface)
    }

    private val colors by lazy {
        with(context.resources) {
//            getIntArray(R.array.liberty_colors) +
            getIntArray(R.array.vordiplom_colors) +
                    getIntArray(R.array.pastel_colors) +
                    getIntArray(R.array.colorful_colors) +
                    getIntArray(R.array.joyful_colors) +
                    ColorTemplate.getHoloBlue()
        }
    }

    private val fillDrawable by lazy {
        GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                ContextCompat.getColor(context, R.color.liberty_color_2),
                surface
            )
        )
    }

    fun bind() = with(chart) {
        setViewPortOffsets(0f, 0f, 0f, 0f)
        setBackgroundColor(surface)
        setOnChartValueSelectedListener(onChartValueSelectedListener)

        // no description text
        description.isEnabled = false

        // enable touch gestures
        setTouchEnabled(true)

        // enable scaling and dragging
        isDragEnabled = true
        setScaleEnabled(true)
        dragDecelerationFrictionCoef = 0.9f
        chart.setDrawGridBackground(false)
        chart.isHighlightPerDragEnabled = true

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true)
        /*        setPinchZoom(false)
        setDrawGridBackground(false)
        maxHighlightDistance = 300f
        isHighlightPerTapEnabled = true*/

        // modify the legend ...
        legend.apply {
            isEnabled = true
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.VERTICAL
            textColor = onSurface
            form = LegendForm.LINE
            formLineWidth = 9f
            formSize = 15f
            setDrawInside(false)
            textSize = 12f
        }

        axisLeft.apply {
            isEnabled = true
//            axisMinimum = 0f
            typeface = Typeface.DEFAULT
//            setLabelCount(6, false)
            textColor = onSurface
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            setDrawGridLines(true)
            setDrawAxisLine(true)
            setDrawLabels(true)
            axisLineColor = onSurface
            isGranularityEnabled = true
        }
        axisRight.isEnabled = false

        // marker
        marker = LineChartMarkerView(context).also {
            it.chartView = this@with // For bounds control
        }

        // chart.setDrawYLabels(false);

        xAxis.apply {
            isEnabled = true
            position = XAxisPosition.BOTTOM
            typeface = Typeface.DEFAULT
            setDrawGridLines(true)
//            granularity = 1f // only intervals of 1 day
//            labelCount = 7
//            axisMinimum = 0f
            valueFormatter = DayAxisValueFormatter2()//(this@with)
        }

        // don't forget to refresh the drawing
        invalidate()
    }

    fun generateData(count: Int, range: Float): List<Entry> = List(count) {
        Entry(
            (19000000 + it).toFloat(),
            (Math.random() * range).toFloat(),
            "Name $it"
        )
    }

    fun setData(entries: List<Entry>, datasetIndex: Int) {
        if (entries.isEmpty()) return

        val set = if (chart.data != null && chart.data.dataSetCount > 0) {
            chart.data.getDataSetByIndex(datasetIndex) as? LineDataSet
        } else null

        if (set != null) {
            set.entries = entries
            Timber.e("ERROR NAXUY | set.entries = ${set.entries}")
        } else {
            // create a dataset and give it a type
            val dataSet = LineDataSet(entries, entries[0].data as String).apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.2f
                setDrawFilled(true)
                setDrawCircles(false)
                setDrawValues(false)
                lineWidth = 1.8f
                circleRadius = 4f
                setCircleColor(surface)
                highLightColor = Color.rgb(244, 117, 117)
                highlightLineWidth = 3f
                setDrawVerticalHighlightIndicator(true)
                setDrawHorizontalHighlightIndicator(true)
                color = this@ChartLineBinder.colors.let { it[datasetIndex % it.size] }
//                fillColor =
                if (datasetIndex == 0) {
                    fillDrawable = this@ChartLineBinder.fillDrawable
                    fillAlpha = 100
                    fillFormatter = IFillFormatter { _, _ ->
                        chart.axisLeft.axisMinimum
                    }
                }
            }
            // set data
            if (chart.data == null) {
                chart.data = LineData(dataSet).apply {
                    setValueTypeface(Typeface.DEFAULT)
                    setValueTextSize(10f)
                    setValueTextColor(onSurface)
                    setDrawValues(false)
                }
            } else {
                chart.data.addDataSet(dataSet)
            }
        }
        chart.animateXY(2000, 2000)
        chart.invalidate()
    }
}