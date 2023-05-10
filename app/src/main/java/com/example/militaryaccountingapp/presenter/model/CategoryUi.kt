package com.example.militaryaccountingapp.presenter.model

data class CategoryUi(
    val id: Int,
    val name: String,
    val description: String = "",
    val itemsCount: Int = 0,
    val nestedCount: Int = 0,
    val allCount: Int = 0,
    val imageUrl: String = "",
    val color: String = "#FFFFFF",
    val usersAvatars: List<String> = listOf(),
    val qrCode: String? = null,
    val parentId: Int? = null,
)