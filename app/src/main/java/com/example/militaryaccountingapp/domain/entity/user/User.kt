package com.example.militaryaccountingapp.domain.entity.user

data class User(
    val id: String,
    val name: String,
    val email: String,
    val imageUrl: String,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long? = null,
)
