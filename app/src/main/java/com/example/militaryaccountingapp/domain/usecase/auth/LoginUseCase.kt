package com.example.militaryaccountingapp.domain.usecase.auth

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.AuthRepository

open class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Results<User> = authRepository.login(email = email, password = password)
}