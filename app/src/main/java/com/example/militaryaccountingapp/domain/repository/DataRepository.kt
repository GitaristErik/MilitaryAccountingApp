package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results

interface DataRepository {

    enum class SortFilter {
        DESCRIPTION,
        DATE_CREATED,
        DATE_UPDATED,
        NAME,
    }


    suspend fun getAllDataByUserId(userId: String): Results<Pair<List<Data>, List<String>>>

    suspend fun getDataByParent(
        clazz: Class<out Data>,
        parentId: String?,
        userId: String,
        sortType: SortFilter = SortFilter.NAME,
        isAscending: Boolean = true,
    ): Results<Map<Data, List<UserPermission>>>

}