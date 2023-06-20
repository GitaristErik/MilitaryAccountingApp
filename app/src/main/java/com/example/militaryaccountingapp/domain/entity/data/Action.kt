package com.example.militaryaccountingapp.domain.entity.data

data class Action(
//    val id: String = "",
    val action: ActionType = ActionType.CREATE,
    val userId: String = "",
    val itemId: String? = "",
    val categoryId: String? = "",
    var timestamp: Long = 0,
    val value: Any? = null
)
