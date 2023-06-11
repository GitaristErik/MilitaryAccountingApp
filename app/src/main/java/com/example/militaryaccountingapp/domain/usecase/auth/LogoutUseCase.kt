package com.example.militaryaccountingapp.domain.usecase.auth

import com.example.militaryaccountingapp.domain.repository.AuthRepository

open class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() =
        authRepository.logout()
}