package com.example.militaryaccountingapp.presenter.fragment.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.databinding.FragmentStatisticsBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.statistics.StatisticsViewModel.ViewData

class StatisticsFragment :
    BaseViewModelFragment<FragmentStatisticsBinding, ViewData, StatisticsViewModel>() {

    override val viewModel: StatisticsViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStatisticsBinding
        get() = FragmentStatisticsBinding::inflate

    override fun initializeView() {
    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}