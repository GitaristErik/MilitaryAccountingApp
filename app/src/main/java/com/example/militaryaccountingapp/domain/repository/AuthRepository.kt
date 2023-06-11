package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Result

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    suspend fun register(
        email: String,
        password: String,
        login: String,
        name: String,
        fullName: String = "",
        rank: String = "",
        phones: List<String> = emptyList(),
    ): Result<User>

    suspend fun signInGoogle(
        idToken: String,
        accessToken: String?
    ): Result<User>

    suspend fun signInFacebook(
        token: String
    ): Result<User>

    suspend fun resetPassword(
        email: String
    ): Result<Void?>

    suspend fun currentUser(): User?

    suspend fun logout()
}