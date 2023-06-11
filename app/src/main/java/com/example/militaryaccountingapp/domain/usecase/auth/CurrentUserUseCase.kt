package com.example.militaryaccountingapp.domain.usecase.auth

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.repository.AuthRepository

open class CurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    open suspend operator fun invoke(): User? = authRepository.currentUser()
}