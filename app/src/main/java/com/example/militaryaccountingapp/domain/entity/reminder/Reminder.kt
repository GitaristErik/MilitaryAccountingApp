package com.example.militaryaccountingapp.domain.entity.reminder

data class Reminder(
    val id: String,
    val itemId: String,
    val date: Long,
    val time: Long?,
)