package com.example.militaryaccountingapp.domain.entity.data

sealed class Data {
    abstract val id: Int
    abstract val name: String
    abstract val description: String
    abstract val imageUrl: String?
    open val parentId: Int? = null
    open val allParentIds: List<Int>? = null
}