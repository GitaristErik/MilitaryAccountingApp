package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results

interface UserRepository {

    suspend fun getUser(id: String): Results<User>


    suspend fun getUsers(ids: List<String>): Results<List<User>>

    /**
     * @return map of userId to avatarUrl
     */
    suspend fun getUsersAvatars(usersIds: List<String>): Map<String, String>

    suspend fun searchUsers(cleanText: String): Results<List<User>>

    suspend fun updateCurrentUserInfo(id: String, mapOf: Map<String, List<Any>>): Results<Unit>

}