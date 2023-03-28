package com.example.militaryaccountingapp.presenter.fragment.home

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.home.HomeViewModel.ViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
    }

    class ViewData
}