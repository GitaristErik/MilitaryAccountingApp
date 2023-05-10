package com.example.militaryaccountingapp.presenter.shared.chart

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.example.militaryaccountingapp.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

@SuppressLint("ViewConstructor")
class LineChartMarkerView(context: Context) : MarkerView(context, R.layout.chart_marker_view) {
    private val textView: TextView = findViewById(R.id.marker_text)

    override fun refreshContent(entry: Entry, highlight: Highlight) {
        val entries = listOf(
            "${entry.x}",
            "Items: ${entry.y}",
        )
        val content = entries.joinToString(separator = "\n")
        textView.text = content
        textView.textAlignment = TEXT_ALIGNMENT_CENTER
        super.refreshContent(entry, highlight)
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }
}