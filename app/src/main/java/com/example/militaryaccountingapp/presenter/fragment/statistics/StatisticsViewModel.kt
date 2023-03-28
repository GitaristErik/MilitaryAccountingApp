package com.example.militaryaccountingapp.presenter.fragment.statistics

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.statistics.StatisticsViewModel.ViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(): BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
    }

    class ViewData
}