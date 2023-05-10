package com.example.militaryaccountingapp.presenter.fragment.item

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.item.ItemViewModel.ViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor() : BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
    }

    class ViewData
}