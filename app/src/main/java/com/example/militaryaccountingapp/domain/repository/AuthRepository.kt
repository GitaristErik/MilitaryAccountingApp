package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Results<User>

    suspend fun register(
        email: String,
        password: String,
//        login: String,
        name: String,
        fullName: String = "",
        rank: String = "",
        phones: List<String> = emptyList(),
    ): Results<User>

    suspend fun signInGoogle(
        idToken: String,
        accessToken: String?
    ): Results<User>

    suspend fun signInFacebook(
        token: String
    ): Results<User>

    suspend fun resetPassword(
        email: String
    ): Results<Void?>

    suspend fun currentUser(): User?

    suspend fun logout()

    suspend fun editUserInfo(
        email: String,
        password: String? = null,
        name: String,
        fullName: String,
        rank: String,
        phones: List<String>
    ): Results<User>

}