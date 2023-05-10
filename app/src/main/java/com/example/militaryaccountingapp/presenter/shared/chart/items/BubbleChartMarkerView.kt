package com.example.militaryaccountingapp.presenter.shared.chart.items

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.example.militaryaccountingapp.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

@SuppressLint("ViewConstructor")
class BubbleChartMarkerView(context: Context) : MarkerView(context, R.layout.chart_marker_view) {
    private val textView: TextView = findViewById(R.id.marker_text)

    override fun refreshContent(entry: Entry, highlight: Highlight) {
        if (entry is BubbleEntry) {
            val entries = if (entry.y.toInt() == 0) {
                listOf(
                    entry.data as String,
                    "Count: ${entry.size.toInt()}",
                )
            } else {
                listOf(
                    entry.data as String,
                    "Count All: ${entry.size.toInt()}",
                    "Nested Items: ${entry.y.toInt()}",
                )
            }
            val content = entries.joinToString(separator = "\n")
            textView.text = content
        }
        super.refreshContent(entry, highlight)
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }
}