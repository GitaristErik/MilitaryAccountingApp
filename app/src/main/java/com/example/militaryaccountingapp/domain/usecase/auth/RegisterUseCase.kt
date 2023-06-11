package com.example.militaryaccountingapp.domain.usecase.auth

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.AuthRepository

open class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        login: String,
        name: String,
        fullName: String = "",
        rank: String = "",
        phones: List<String> = emptyList(),
    ): Results<User> =
        authRepository.register(
            email,
            password,
            login,
            name,
            fullName,
            rank,
            phones,
        )
}