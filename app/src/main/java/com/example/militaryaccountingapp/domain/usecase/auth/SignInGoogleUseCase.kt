package com.example.militaryaccountingapp.domain.usecase.auth

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.domain.repository.AuthRepository

open class SignInGoogleUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        idToken: String,
        accessToken: String?
    ): Result<User> = authRepository.signInGoogle(idToken, accessToken)
}