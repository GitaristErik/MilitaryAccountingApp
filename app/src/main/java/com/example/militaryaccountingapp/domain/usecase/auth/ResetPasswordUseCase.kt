package com.example.militaryaccountingapp.domain.usecase.auth

import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.AuthRepository

open class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Results<Void?> =
        authRepository.resetPassword(email)
}