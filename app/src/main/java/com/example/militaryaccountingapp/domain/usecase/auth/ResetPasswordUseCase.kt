package com.example.militaryaccountingapp.domain.usecase.auth

import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.domain.repository.AuthRepository

open class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Void?> =
        authRepository.resetPassword(email)
}