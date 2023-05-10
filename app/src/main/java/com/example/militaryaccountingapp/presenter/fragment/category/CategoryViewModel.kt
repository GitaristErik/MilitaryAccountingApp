package com.example.militaryaccountingapp.presenter.fragment.category

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.category.CategoryViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.chart.history.ChartData
import com.example.militaryaccountingapp.presenter.shared.chart.history.DayData
import com.example.militaryaccountingapp.presenter.shared.chart.history.HistoryChartItem
import com.example.militaryaccountingapp.presenter.shared.chart.history.MonthData
import com.example.militaryaccountingapp.presenter.shared.chart.history.WeekData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class CategoryViewModel @Inject constructor(): BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
    }

    data class ViewData (
        val historyChartType: ChartData? = null,
    )


    fun changeHistoryChartType(chartType: KClass<out ChartData>?) {
        val data = List(10) {
            HistoryChartItem(
                LocalDate.of(2023, 5, it + 1).toString(),
                (Math.random() * 100).toInt()
            )
        }

        val chart = when (chartType) {
            DayData::class -> DayData(data)
            WeekData::class -> WeekData(mapToWeeks(data))
            MonthData::class -> MonthData(data)
            else -> null
        }?.apply { generate() }

        _data.update { it.copy(historyChartType = chart) }
    }


    private fun mapToWeeks(dates: List<HistoryChartItem>): List<HistoryChartItem> =
        dates.groupBy {
            it.date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        }.map { (sunday, dates) ->
            val items = dates.sumOf { it.items }
            HistoryChartItem(sunday.toString(), items)
        }.sortedBy { it.date }
}