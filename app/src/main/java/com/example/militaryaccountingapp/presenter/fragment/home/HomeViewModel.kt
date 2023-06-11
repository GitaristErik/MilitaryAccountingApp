package com.example.militaryaccountingapp.presenter.fragment.home

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.home.HomeViewModel.ViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val currentUserUseCase: CurrentUserUseCase,
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val currentUser: Result<User?> = Result.Loading(null),
    )

    init {
        checkUser()
    }

    fun checkUser() {
        safeRunJobWithLoading(Dispatchers.IO) {
            _data.update {
                it.copy(currentUser = Result.Success(currentUserUseCase()))
            }
        }
    }
}