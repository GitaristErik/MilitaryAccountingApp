package com.example.militaryaccountingapp.presenter.fragment.category

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.category.CategoryViewModel.ViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(): BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
    }

    class ViewData
}