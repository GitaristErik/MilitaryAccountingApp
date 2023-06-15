package com.example.militaryaccountingapp.presenter.model

data class UserSearchUi(
    val id: String,
    val rank: String,
    val name: String,
    val fullName: String,
    val imageUrl: String? = null,
)