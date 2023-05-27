package com.example.militaryaccountingapp.presenter.model

import android.graphics.drawable.Drawable
import com.example.militaryaccountingapp.domain.entity.data.ActionType

data class TimelineUi(
    val date: String,
    val title: String,
    val value: String,
    val operation: ActionType,
    val location: String,
    val name: String,
    val userIcon: Drawable? = null
)