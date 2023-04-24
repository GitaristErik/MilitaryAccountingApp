package com.example.militaryaccountingapp.domain.entity.data

data class Item(
    val id: Int,
    val name: String,
    val description: String,
    val count: Int,
    val categoryId: Int?,
    val imageUrl: String?,
    val qrCode: String?,
    val barCode: String?,
)