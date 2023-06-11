package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.User

interface DataRepository {

    enum class SortFilter {
        DESCRIPTION,
        DATE_CREATED,
        DATE_UPDATED,
        NAME,
    }

    suspend fun getHistory(
        limit: Int = -1,
        page: Int = 0,
        filters: Set<ActionType> = emptySet(),
    ): List<Triple<Action, Data?, User>>

    suspend fun search(
        query: String,
        limit: Int = -1,
        filter: SortFilter = SortFilter.NAME,
        isAscending: Boolean = true,
        usersIds: List<String> = emptyList(),
    ): List<Data>

}