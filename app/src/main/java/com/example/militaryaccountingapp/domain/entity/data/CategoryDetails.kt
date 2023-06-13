package com.example.militaryaccountingapp.domain.entity.data

data class CategoryDetails(
    val id: String = "",
    val nestedCount: Int = 0, // second level nested items and categories count
    val itemCount: Int = 0,
    val allItemsSum: Int = 0,
)
