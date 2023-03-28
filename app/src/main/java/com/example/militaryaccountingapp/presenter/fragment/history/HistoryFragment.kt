package com.example.militaryaccountingapp.presenter.fragment.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.databinding.FragmentHistoryBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment

class HistoryFragment :
    BaseViewModelFragment<FragmentHistoryBinding, HistoryViewModel.ViewData, HistoryViewModel>() {

    override val viewModel: HistoryViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHistoryBinding
        get() = FragmentHistoryBinding::inflate

    override fun initializeView() {
    }

    override fun render(data: HistoryViewModel.ViewData) {
        log.d("render")
    }
}