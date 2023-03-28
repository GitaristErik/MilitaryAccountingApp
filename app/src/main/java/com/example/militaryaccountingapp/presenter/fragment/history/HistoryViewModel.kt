package com.example.militaryaccountingapp.presenter.fragment.history

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.history.HistoryViewModel.ViewData
import timber.log.Timber

class HistoryViewModel : BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
    }

    class ViewData
}