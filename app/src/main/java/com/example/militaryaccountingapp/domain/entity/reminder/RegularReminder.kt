package com.example.militaryaccountingapp.domain.entity.reminder

import java.time.DayOfWeek

data class RegularReminder(
    val id: String,
    val itemId: String,
    val dayOfWeek: DayOfWeek,
    val time: Long?,
)