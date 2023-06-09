package com.example.militaryaccountingapp.presenter.fragment.profile

import android.net.Uri
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.CroppingSavableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel<ViewData>(ViewData()),
    CroppingSavableViewModel {

    data class ViewData(
        val userProfileUri: Uri? = null
    )

    init {
        log.d("init")
    }

    fun setAvatar(uri: Uri) {
        log.d("setAvatar uri: $uri")
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

    override fun saveCropUri(uri: Uri) {
        log.d("saveCropUri $uri")
        setAvatar(uri)
    }
}