package com.example.militaryaccountingapp.domain.entity.user

import java.io.Serializable

data class UserPermission(
    var userId: String = "",
    val itemId: String? = null,
    val categoryId: String? = null,
    val canWrite: Boolean = true,
    val canShare: Boolean = true,
    val canShareForWrite: Boolean = true
) : Serializable