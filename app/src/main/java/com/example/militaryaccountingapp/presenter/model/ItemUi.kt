package com.example.militaryaccountingapp.presenter.model

data class ItemUi(
    val id: String,
    val name: String,
    val description: String = "",
    val count: Int = 0,
    val imageUrl: String = "",
    val color: String = "#FFFFFF",
    val usersAvatars: List<String> = listOf(),
    val qrCode: String? = null,
    val parentId: String? = null,
)