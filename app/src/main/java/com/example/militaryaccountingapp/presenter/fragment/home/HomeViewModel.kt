package com.example.militaryaccountingapp.presenter.fragment.home

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.home.HomeViewModel.ViewData
import timber.log.Timber

class HomeViewModel : BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
    }

    class ViewData
}