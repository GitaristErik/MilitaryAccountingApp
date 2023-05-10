package com.example.militaryaccountingapp.presenter.fragment.statistics

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.statistics.StatisticsViewModel.ViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class StatisticsViewModel @Inject constructor() : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val chartType: ChartType? = null,
    )

    init {
        Timber.d("init")
        fetch()
    }

    private fun fetch() {
        log.d("fetch")
    }

    fun changeChartType(chartType: KClass<out ChartType>?) {
        val chart = when (chartType) {
            ChartType.Count::class -> ChartType.Count()
            ChartType.Users::class -> ChartType.Users()
            else -> null
        }

        _data.update { it.copy(chartType = chart) }
    }


    sealed class ChartType() {

        class Count() : ChartType()

        class Users() : ChartType()

    }
}
