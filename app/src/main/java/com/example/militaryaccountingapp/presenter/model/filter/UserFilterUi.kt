package com.example.militaryaccountingapp.presenter.model.filter

data class UserFilterUi(
    val id: String = "",
    val name: String = "",
    val imageUrl: String? = null,
    val count: Int = 0,
    val checked: Boolean = true,
)