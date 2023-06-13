package com.example.militaryaccountingapp.domain.usecase.auth

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.repository.AuthRepository
import javax.inject.Inject

open class CurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    open suspend operator fun invoke(): User? = authRepository.currentUser()
}