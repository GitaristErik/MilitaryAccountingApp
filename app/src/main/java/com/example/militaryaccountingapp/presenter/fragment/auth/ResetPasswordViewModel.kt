package com.example.militaryaccountingapp.presenter.fragment.auth

import com.example.militaryaccountingapp.domain.helper.Results
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
        val email: Results<String> = Results.Loading(""),
        val isReset: Results<Boolean> = Results.Loading(false),
    )

    fun reset(email: String) {
        AuthValidator.EmailValidator.validate(email).let { emailResult ->
            _data.update {
                it.copy(email = emailResult, isReset = Results.Success(false))
            }
            if (emailResult is Results.Success) {
                safeRunJobWithLoading(Dispatchers.IO) {
                    val res = resultWrapper(
                        resetPasswordUseCase(email)
                    ) {
                        Results.Success(true)
                    }
                    _data.update { it.copy(isReset = res) }
                }
            }
        }
    }
}