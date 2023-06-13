package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.helper.Results

interface HistoryRepository {

    enum class ActionElement {
        ITEM, CATEGORY
    }

    suspend fun getLastAction(
        id: String,
        filters: Set<ActionType> = emptySet(),
        element: ActionElement
    ): Results<Action>

}