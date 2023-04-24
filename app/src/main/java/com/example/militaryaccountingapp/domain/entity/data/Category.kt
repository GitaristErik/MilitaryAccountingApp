package com.example.militaryaccountingapp.domain.entity.data

data class Category(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val parentId: Int?,
    val itemIds: List<Int>,
    val subcategoryIds: List<Int>
)