package com.example.militaryaccountingapp.presenter.model.filter

data class UserFilterUi(
    val id: Int,
    val name: String,
    val imageUrl: String = "",
    val count: Int = 0,
    val checked: Boolean = true,
)