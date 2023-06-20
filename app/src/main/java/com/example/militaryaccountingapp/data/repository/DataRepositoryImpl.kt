package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.DataRepository
import com.example.militaryaccountingapp.domain.repository.DataRepository.SortFilter
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor() : DataRepository {

    override suspend fun getAllDataByUserId(userId: String): Results<Pair<List<Data>, List<String>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getDataByParent(
        clazz: Class<out Data>,
        parentId: String?,
        userId: String,
        sortType: SortFilter,
        isAscending: Boolean
    ): Results<Map<Data, List<UserPermission>>> {
        TODO("Not yet implemented")
    }

}