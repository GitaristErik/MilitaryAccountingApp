package com.example.militaryaccountingapp.domain.usecase.auth

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.domain.repository.AuthRepository

open class SignInFacebookUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        token: String
    ): Result<User> = authRepository.signInFacebook(token)
}