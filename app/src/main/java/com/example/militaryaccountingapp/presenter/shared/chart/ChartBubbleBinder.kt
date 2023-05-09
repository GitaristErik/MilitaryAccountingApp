package com.example.militaryaccountingapp.presenter.shared.chart

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.example.militaryaccountingapp.R
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF

class ChartBubbleBinder(
    private val context: Context,
    private val chart: BubbleChart,
    private val listener: OnChartValueSelectedListener
) {

    fun bind() {
        chart.apply {
            description.isEnabled = false
            setOnChartValueSelectedListener(listener)
            setDrawGridBackground(false)

            // enable scaling and dragging
            setTouchEnabled(true)
            setBackgroundColor(ContextCompat.getColor(context, R.color.md_surface))
            isDragEnabled = true
            setScaleEnabled(true)
            isScaleXEnabled = true
            isScaleYEnabled = true
            setPinchZoom(true)
            // chart.setMaxVisibleValueCount(200)
        }

        // legend
        chart.legend.isEnabled = false
        /*        chart.legend.apply {
                    isEnabled = true
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    orientation = Legend.LegendOrientation.VERTICAL
                    setDrawInside(false)
                    typeface = Typeface.DEFAULT
                }*/
        chart.animateY(2000)

        chart.axisLeft.apply {
            typeface = Typeface.DEFAULT
            setDrawZeroLine(true)
            textColor = ContextCompat.getColor(context, R.color.md_onSurface)
            axisMinimum = 0f
            valueFormatter = IAxisValueFormatter { value, _ ->
                value.toInt().toString()
            }
        }

        chart.axisRight.isEnabled = false
        chart.xAxis.isEnabled = false

        /*        chart.xAxis.apply {
                    isEnabled = true
                    position = XAxis.XAxisPosition.BOTTOM
                    typeface = Typeface.DEFAULT
                    axisMinimum = 0f
                }*/

        chart.marker = BubbleChartMarkerView(context).also {
            it.chartView = chart // For bounds control
        }

    }

    fun generateData(data: List<TestData>): List<BubbleEntry> = data.mapIndexed { i, item ->
        BubbleEntry(
            (i % 25).toFloat(),
            item.itemsCount.toFloat(),
            item.count.toFloat(),
            item.name
        )
    }

    fun setData(entries: List<BubbleEntry>) {

        val dataSet = BubbleDataSet(entries, "").apply {
            setDrawIcons(false)
            setDrawValues(true)
            iconsOffset = MPPointF(0f, 40f)
            // add a lot of colors
            setColors(with(context.resources) {
                getIntArray(R.array.vordiplom_colors) +
                        getIntArray(R.array.joyful_colors) +
                        getIntArray(R.array.colorful_colors) +
                        getIntArray(R.array.liberty_colors) +
                        getIntArray(R.array.pastel_colors) +
                        ColorTemplate.getHoloBlue()
            }, 130)
        }

        // create a data object with the data sets
        chart.data = BubbleData(dataSet).apply {
            setDrawValues(false)
            setValueTypeface(Typeface.DEFAULT)
            setValueTextSize(8f)
            setHighlightCircleWidth(1.5f)
            setValueTextColor(ContextCompat.getColor(context, R.color.md_onBackground))
//            setValueTextColor(Color.WHITE)
//            setValueTextSize(11f)
//            setValueFormatter(PercentFormatter())
        }

        chart.invalidate()
    }
}

data class TestData(
    val id: Int,
    val name: String,
    var count: Int,
    var itemsCount: Int = 0
) {
    companion object {

        fun generateTestData(
            id: Int = 123,
            name: String = "Root",
            count: Int = 100,
            itemsCount: Int = 3,
            level: Int = 0,
            maxLevel: Int = 1
        ): List<TestData> {
            val list = mutableListOf<TestData>()
            list.add(TestData(id, name, count, itemsCount))
            if (level < maxLevel) {
                val categoriesCount = (Math.random() * 10).toInt()
                val itemsCount = (Math.random() * 10).toInt()
                for (i in 0 until categoriesCount) {
                    list.addAll(
                        generateTestData(
                            id + i + 1,
                            name + " " + (i + 1),
                            count + (Math.random() * 100).toInt(),
                            itemsCount = itemsCount,
                            level = level + 1,
                            maxLevel = maxLevel
                        )
                    )
                }
                for (i in 0 until itemsCount) {
                    list.add(
                        TestData(
                            id + categoriesCount + i + 1,
                            name + " " + (categoriesCount + i + 1),
                            count + (Math.random() * 100).toInt(),
                            itemsCount + (Math.random() * 5).toInt()
                        )
                    )
                }
            }
            return list
        }

    }
}