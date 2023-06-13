package com.example.militaryaccountingapp.domain.entity.user

data class User(
    val id: String = "",
    val login: String = "",
    val email: String = "",
    var rootCategoryId: String = "",
    val name: String = "",
    val fullName: String = "",
    val rank: String = "",
    val imageUrl: String? = null,
    val phones: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val deletedAt: Long? = null,
)
