package com.example.militaryaccountingapp.presenter.fragment.edit

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddOrEditViewModel @Inject constructor(
) : BaseViewModel<ViewData>(ViewData()) {

    class ViewData

    init {
        Timber.d("init")
    }

    companion object {
        private const val DELAY = 400L
    }
}