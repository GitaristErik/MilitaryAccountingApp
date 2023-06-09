package com.example.militaryaccountingapp.presenter.model

import com.example.militaryaccountingapp.domain.entity.data.ActionType

data class TimelineUi(
    val date: String,
    val title: String,
    val value: String,
    val operation: ActionType,
    val location: String,
    val name: String,
    val userIcon: String? = null
)