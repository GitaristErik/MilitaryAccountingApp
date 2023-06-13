package com.example.militaryaccountingapp.domain.entity.user

data class UserPermission(
    var userId: String = "",
    val itemId: String? = null,
    val categoryId: String? = null,
    val isCanWrite: Boolean = false,
    val isCanShare: Boolean = false,
    val isCanShareForWrite: Boolean = false
)