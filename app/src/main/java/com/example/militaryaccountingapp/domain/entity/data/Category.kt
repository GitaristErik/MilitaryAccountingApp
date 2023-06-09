package com.example.militaryaccountingapp.domain.entity.data

data class Category(
    override val id: Int,
    override val name: String,
    override val description: String,
    override val imageUrl: String,
    val itemIds: List<Int>,
    override val parentId: Int?,
    override val allParentIds: List<Int>?,
    val subcategoryIds: List<Int>
) : Data()