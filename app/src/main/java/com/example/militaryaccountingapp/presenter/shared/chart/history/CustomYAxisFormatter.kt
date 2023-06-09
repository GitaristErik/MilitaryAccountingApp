package com.example.militaryaccountingapp.presenter.shared.chart.history

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

internal class CustomYAxisFormatter : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase): String = value.toInt().toString()
}