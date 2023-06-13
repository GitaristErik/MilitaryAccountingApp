package com.example.militaryaccountingapp.presenter.fragment.profile

import android.net.Uri
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.EditUserInfoUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.LogoutUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.CroppingSavableViewModel
import com.example.militaryaccountingapp.presenter.utils.ui.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val editUserInfoUseCase: EditUserInfoUseCase,
    private val currentUserUseCase: CurrentUserUseCase
) : BaseViewModel<ViewData>(ViewData()), CroppingSavableViewModel {

    data class ViewData(
        val email: Results<String> = Results.Loading(""),
//        val password: Results<String> = Results.Loading(""),
//        val repassword: Results<String> = Results.Loading(""),
        val login: Results<String> = Results.Loading(""),
        val name: Results<String> = Results.Success(""),
        val fullName: Results<String> = Results.Success(""),
        val rank: Results<String> = Results.Success(""),
        val phones: List<Results<String>> = emptyList(),

        val user: Results<User> = Results.Loading(User()),
        val userProfileUri: Uri? = null,
        val isLoggingOut: Results<Unit> = Results.Canceled(null),
        val isEdited: Results<Unit> = Results.Canceled(null),
    )

    init {
        log.d("init")
        fetchUserData()
    }

    fun fetchUserData() {
        safeRunJobWithLoading(Dispatchers.IO) {
            _data.update { viewData ->
                currentUserUseCase().let {
                    if (it != null) {
                        viewData.copy(
                            name = Results.Success(it.name),
                            email = Results.Success(it.email),
                            login = Results.Success(it.login),
                            fullName = Results.Success(it.fullName),
                            rank = Results.Success(it.rank),
                            phones = it.phones.map { phone -> Results.Success(phone) },
                            user = Results.Success(it),
                            isEdited = Results.Canceled(null)
                        )
                    } else {
                        viewData.copy(
                            user = Results.Failure(Exception("User is null")),
                            isEdited = Results.Failure(Exception("User is null"))
                        )
                    }
                }
            }
        }
    }


    fun logout() {
        log.d("logout")
        safeRunJob(Dispatchers.IO) {
            _data.update {
                it.copy(isLoggingOut = Results.Success(logoutUseCase()))
            }
        }
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

    fun save(
        email: String,
        login: String,
        name: String,
        fullName: String,
        rank: String,
        phones: List<String>
    ) {
        val emailResult = AuthValidator.EmailValidator.validate(email)
//        val passwordResult = AuthValidator.PasswordValidator.validate(password)
//        val repasswordResult = AuthValidator.RepasswordValidator.validate(repassword)
        val loginResult = AuthValidator.LoginValidator.validate(login)
        val nameResult = AuthValidator.NameValidator.validate(name)
        val fullNameResult = AuthValidator.FullNameValidator.validate(fullName)
        val rankResult = AuthValidator.RankValidator.validate(rank)
        val phonesResult = AuthValidator.PhonesValidator.validate(phones)

        _data.update {
            it.copy(
                email = emailResult,
//                password = passwordResult,
//                repassword = repasswordResult,
                login = loginResult,
                name = nameResult,
                fullName = fullNameResult,
                rank = rankResult,
                phones = phonesResult,
                isEdited = Results.Canceled(null),
            )
        }

        listOf<Results<String>>(
            emailResult,
//            passwordResult,
//            repasswordResult,
            loginResult,
            nameResult,
            fullNameResult,
            rankResult,
            *phonesResult.toTypedArray()
        ).all { it is Results.Success<*> }.let { result ->
            if (!result) return
            safeRunJobWithLoading(Dispatchers.IO) {
                val res = resultWrapper(
                    editUserInfoUseCase(
                        email = email,
                        login = login,
                        name = name,
                        fullName = fullName,
                        rank = rank,
                        phones = phones,
                    )
                ) { Results.Success(Unit) }
                _data.update { it.copy(isEdited = res) }
            }
        }
    }
}