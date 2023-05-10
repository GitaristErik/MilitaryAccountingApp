package com.example.militaryaccountingapp.domain.entity.user

data class UserPermission(
    val userId: Int,
    val itemId: Int?,
    val categoryId: Int?,
    val isCanWrite: Boolean,
    val isCanShare: Boolean,
    val isCanShareForWrite: Boolean
)