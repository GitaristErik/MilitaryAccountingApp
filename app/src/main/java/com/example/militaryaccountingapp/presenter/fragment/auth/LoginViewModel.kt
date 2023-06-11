package com.example.militaryaccountingapp.presenter.fragment.auth

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.usecase.auth.LoginUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.SignInFacebookUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.SignInGoogleUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.auth.AuthService.REQ_ONE_TAP
import com.example.militaryaccountingapp.presenter.fragment.auth.LoginViewModel.ViewData
import com.example.militaryaccountingapp.presenter.utils.ui.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signInFacebookUseCase: SignInFacebookUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val email: Results<String> = Results.Loading(""),
        val password: Results<String> = Results.Loading(""),
        val isSigned: Results<Boolean> = Results.Loading(false),
    )

    fun login(email: String, password: String) {
        val emailResult = AuthValidator.EmailValidator.validate(email)
        val passwordResult = AuthValidator.PasswordValidator.validate(password)
        val signedResults: Results<Boolean> = Results.Success(false)
        _data.update {
            it.copy(email = emailResult, password = passwordResult, isSigned = signedResults)
        }

        if (emailResult is Results.Success && passwordResult is Results.Success) {
            safeRunJobWithLoading(Dispatchers.IO) {
                val res = resultWrapper(
                    loginUseCase(email, password)
                ) {
                    Results.Success(true)
                }
                _data.update { it.copy(isSigned = res) }
            }
        }
    }

    // Google
    fun loginWithGoogle(activity: Activity) {
        safeRunJobWithLoading(Dispatchers.IO) {
            AuthService.signInWithGoogle(activity, true) { result ->
                _data.update {
                    it.copy(isSigned = result)
                }
            }
        }
    }

    //Facebook
    fun signInWithFacebook(activity: Activity) {
        safeRunJobWithLoading(Dispatchers.IO) {
            AuthService.signInWithFacebook(activity) { loginResult ->
                viewModelScope.launch(Dispatchers.IO) {
                    val res = resultWrapper(loginResult) {
                        resultWrapper(
                            signInFacebookUseCase(it.accessToken.token)
                        ) {
                            Results.Success(true)
                        }
                    }
                    _data.update { it.copy(isSigned = res) }
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        AuthService.callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_ONE_TAP) {
            val res = AuthService.handleGoogleAccessToken(data) { idToken ->
                safeRunJobWithLoading(Dispatchers.IO) {
                    _data.update {
                        it.copy(isSigned = resultWrapper(
                            signInGoogleUseCase(idToken, null)
                        ) { Results.Success(true) })
                    }
                }
            }
            _data.update { it.copy(isSigned = res) }
        }
    }
}