package com.example.militaryaccountingapp.presenter.fragment.profile

import android.net.Uri
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.profile.DetailsUserViewModel.ViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailsUserViewModel @Inject constructor() : BaseViewModel<ViewData>(ViewData()) {
    data class ViewData(
        val userProfileUri: Uri? = null
    )

    init {
        Timber.d("init")
    }

    override fun onCleared() {
        super.onCleared()
        log.e("onCleared")
    }
}