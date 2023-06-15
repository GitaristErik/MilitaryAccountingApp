package com.example.militaryaccountingapp.presenter.model

data class UserNetworkUi(
    val id: String,
    val rank: String,
    val fullName: String,
    val imageUrl: String? = null,
    val readCount: Int = 0,
    val readShareCount: Int = 0,
    val editCount: Int = 0,
    val editShareCount: Int = 0,
)