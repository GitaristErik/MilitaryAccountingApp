package com.example.militaryaccountingapp.domain.entity.data

data class Action(
    val id: Int,
    val action: ActionType,
    val userId: Int,
    val itemId: Int?,
    val categoryId: Int?,
    val timestamp: Long,
    val value: Any
)
