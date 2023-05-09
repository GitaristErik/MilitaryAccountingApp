package com.example.militaryaccountingapp.presenter.shared.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.example.militaryaccountingapp.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import timber.log.Timber

class ChartPieBinder(
    private val context: Context,
    private val chart: PieChart,
    private val onChartValueSelectedListener: OnChartValueSelectedListener
) {

    fun bind(
        centerLabel: String = "",
        centerContent: String = ""
    ) = with(chart) {
        setUsePercentValues(true)
        description.isEnabled = false
        setExtraOffsets(0f, 0f, 0f, 0f)
        dragDecelerationFrictionCoef = 0.95f
        setCenterTextTypeface(Typeface.DEFAULT)
        centerText = generateCenterSpannableText(centerLabel, centerContent)
        isDrawHoleEnabled = true
        setTransparentCircleAlpha(110)
        holeRadius = 48f
        transparentCircleRadius = 55f
        setDrawCenterText(true)
        rotationAngle = 0f
        ContextCompat.getColor(context, R.color.md_surface).also {
            setBackgroundColor(it)
            setHoleColor(it)
            setTransparentCircleColor(it)
        }

        // enable rotation of the chart by touch
        isRotationEnabled = true
        isHighlightPerTapEnabled = true

        setOnChartValueSelectedListener(onChartValueSelectedListener)

        animateY(1400, Easing.EaseInOutQuad)

        legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.VERTICAL
            setDrawInside(false)
            yEntrySpace = 0f
            yOffset = 0f
            textSize = 12f
        }

        // entry label styling
//            setEntryLabelTypeface(tfRegular)
        setEntryLabelColor(ContextCompat.getColor(context, R.color.md_onSurface))
        setEntryLabelTextSize(12f)
    }


    fun setData(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "").apply {
            setDrawIcons(false)
            sliceSpace = 3f
            iconsOffset = MPPointF(0f, 40f)
            selectionShift = 5f
            // add a lot of colors
            colors = ColorTemplate.VORDIPLOM_COLORS.asList() +
                    ColorTemplate.JOYFUL_COLORS.asList() +
                    ColorTemplate.COLORFUL_COLORS.asList() +
                    ColorTemplate.LIBERTY_COLORS.asList() +
                    ColorTemplate.PASTEL_COLORS.asList() +
                    ColorTemplate.getHoloBlue()
        }

        chart.data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(11f)
            setValueTypeface(Typeface.DEFAULT)
            setValueTextColor(ContextCompat.getColor(context, R.color.md_onTertiaryContainer))
        }

        // undo all highlights
        chart.highlightValues(null)
        chart.invalidate()
    }


    fun actionToggleSpin() {
        chart.spin(
            1000,
            chart.rotationAngle,
            chart.rotationAngle + 360,
            Easing.EaseInOutCubic
        )
    }


    companion object {
        fun getDatasetEntries(
            count: Int,
            range: Float
        ): List<PieEntry> = List(count) { i ->
            val p = parties[i % parties.size] + ' '
            PieEntry(
                (Math.random() * range + range / 5).toFloat(),
                p + p + p + p,
                // getResources().getDrawable(R.drawable.star) //icon res
            )
        }

        private fun generateCenterSpannableText(label: String, content: String): SpannableString =
            SpannableString("$content\n$label").apply {
                setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, content.length, 0)
                setSpan(RelativeSizeSpan(2f), 0, content.length, 0)
                setSpan(StyleSpan(Typeface.NORMAL), content.length + 1, length, 0)
                setSpan(RelativeSizeSpan(1f), content.length + 1, length, 0)
            }

        private val parties = arrayOf(
            "Party A", "Party B", "Party C",
        )
    }

}

object ListenerValueSelected : OnChartValueSelectedListener {

    override fun onValueSelected(e: Entry, h: Highlight) {
        Timber.i("Value: " + e.y + ", index: " + h.x + ", DataSet index: " + h.dataSetIndex)
    }

    override fun onNothingSelected() {
        Timber.tag("PieChart").i("nothing selected")
    }

}