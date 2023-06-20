package com.example.militaryaccountingapp.domain.entity.user

data class  User(
    val id: String = "",
//    val login: String = "",
    val email: String = "",
    var rootCategoryId: String = "",
    var sharedRootCategories: List<String> = emptyList(),
    var name: String = "",
    var fullName: String = "",
    var rank: String = "",
    val imageUrl: String? = null,
    val phones: List<String> = emptyList(),
    val usersInNetwork: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val deletedAt: Long? = null,
)
