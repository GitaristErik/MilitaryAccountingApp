package com.example.militaryaccountingapp.presenter.fragment.statistics

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.statistics.StatisticsViewModel.ViewData
import timber.log.Timber

class StatisticsViewModel : BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
    }

    class ViewData
}