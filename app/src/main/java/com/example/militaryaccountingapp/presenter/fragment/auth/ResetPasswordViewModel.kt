package com.example.militaryaccountingapp.presenter.fragment.auth

import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.domain.usecase.auth.ResetPasswordUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.auth.ResetPasswordViewModel.ViewData
import com.example.militaryaccountingapp.presenter.utils.ui.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val email: Result<String> = Result.Loading(""),
        val isReset: Result<Boolean> = Result.Loading(false),
    )

    fun reset(email: String) {
        AuthValidator.EmailValidator.validate(email).let { emailResult ->
            _data.update {
                it.copy(email = emailResult, isReset = Result.Success(false))
            }
            if (emailResult is Result.Success) {
                safeRunJobWithLoading(Dispatchers.IO) {
                    val res = resultWrapper(
                        resetPasswordUseCase(email)
                    ) {
                        Result.Success(true)
                    }
                    _data.update { it.copy(isReset = res) }
                }
            }
        }
    }
}