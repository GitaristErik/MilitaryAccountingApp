package com.example.militaryaccountingapp.presenter.fragment.profile

import android.net.Uri
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel<ViewData>(ViewData()) {
    data class ViewData(
        val userProfileUri: Uri? = null
    )

    init {
        Timber.d("init")
    }

    fun setAvatar(uri: Uri) {
        Timber.d("setAvatar uri: $uri")
        _data.update {
            it.copy(userProfileUri = uri)
        }
    }

    override fun onCleared() {
        super.onCleared()
        log.e("onCleared")
    }

    fun deleteAvatar() {
        Timber.d("delete avatar")
        _data.update {
            it.copy(userProfileUri = null)
        }
    }
}