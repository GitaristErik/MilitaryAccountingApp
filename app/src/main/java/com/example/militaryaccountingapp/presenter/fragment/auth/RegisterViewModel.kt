package com.example.militaryaccountingapp.presenter.fragment.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.usecase.auth.RegisterUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.SignInFacebookUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.SignInGoogleUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.auth.AuthService.REQ_ONE_TAP
import com.example.militaryaccountingapp.presenter.fragment.auth.RegisterViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.CroppingSavableViewModel
import com.example.militaryaccountingapp.presenter.utils.ui.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val signInFacebookUseCase: SignInFacebookUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
) : BaseViewModel<ViewData>(ViewData()), CroppingSavableViewModel {

    data class ViewData(
        val email: Results<String> = Results.Loading(""),
        val password: Results<String> = Results.Loading(""),
        val repassword: Results<String> = Results.Loading(""),
        val login: Results<String> = Results.Loading(""),
        val name: Results<String> = Results.Success(""),
        val fullName: Results<String> = Results.Success(""),
        val rank: Results<String> = Results.Success(""),
        val phones: List<Results<String>> = emptyList(),
        val imageUri: Uri? = null,
        val isSigned: Results<Boolean> = Results.Loading(false),
    )

    fun register(
        email: String,
        password: String,
        repassword: String,
        login: String,
        name: String,
        fullName: String,
        rank: String,
        phones: List<String>
    ) {
        val emailResult = AuthValidator.EmailValidator.validate(email)
        val passwordResult = AuthValidator.PasswordValidator.validate(password)
        val repasswordResult = AuthValidator.RepasswordValidator.validate(repassword)
        val loginResult = AuthValidator.LoginValidator.validate(login)
        val nameResult = AuthValidator.NameValidator.validate(name)
        val fullNameResult = AuthValidator.FullNameValidator.validate(fullName)
        val rankResult = AuthValidator.RankValidator.validate(rank)
        val phonesResult = AuthValidator.PhonesValidator.validate(phones)

        _data.update {
            it.copy(
                email = emailResult,
                password = passwordResult,
                repassword = repasswordResult,
                login = loginResult,
                name = nameResult,
                fullName = fullNameResult,
                rank = rankResult,
                phones = phonesResult,
                isSigned = Results.Success(false),
            )
        }

        listOf<Results<String>>(
            emailResult,
            passwordResult,
            repasswordResult,
            loginResult,
            nameResult,
            fullNameResult,
            rankResult,
            *phonesResult.toTypedArray()
        ).all { it is Results.Success<*> }.let { result ->
            if (!result) return
            safeRunJobWithLoading(Dispatchers.IO) {
                val res = resultWrapper(
                    registerUseCase(
                        email = email,
                        password = password,
                        login = login,
                        name = name,
                        fullName = fullName,
                        rank = rank,
                        phones = phones,
                    )
                ) { Results.Success(true) }
                _data.update { it.copy(isSigned = res) }
            }
        }
    }

    override fun saveCropUri(uri: Uri) {
        _data.update { it.copy(imageUri = uri) }
    }

    fun deleteAvatar() {
        _data.update { it.copy(imageUri = null) }
    }

    // Google
    fun registerWithGoogle(activity: Activity) {
        safeRunJobWithLoading(Dispatchers.IO) {
            AuthService.signInWithGoogle(activity, false) { result ->
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