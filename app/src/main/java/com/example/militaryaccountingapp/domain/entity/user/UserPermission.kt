package com.example.militaryaccountingapp.domain.entity.user

import java.io.Serializable

data class UserPermission(
//    var userId: String = "",
    var grantedUsersId: List<String> = emptyList(),
//    val itemId: String? = null,
//    val categoryId: String? = null
    val id: String = "",
    val canWrite: Boolean = true,
    val canShare: Boolean = true,
    val canShareForWrite: Boolean = true
) : Serializable