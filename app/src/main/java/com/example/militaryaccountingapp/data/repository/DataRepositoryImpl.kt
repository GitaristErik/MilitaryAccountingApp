package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.repository.DataRepository
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor() : DataRepository {
    override suspend fun getHistory(
        limit: Int,
        page: Int,
        filters: Set<ActionType>,
    ): List<Triple<Action, Data?, User>> {
        return emptyList()
    }

}